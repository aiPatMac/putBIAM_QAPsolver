package com.mycompany.qapsolver;

import java.io.*;
import java.util.*;

public class ExperimentRunner3 {
    public interface AlgorithmFactory {
        String getName();
        Algorithm create(Problem problem);
    }

    private final List<AlgorithmFactory> algorithmFactories;
    private final String instancesDir;
    private final int maxInstances;
    private final int maxRestarts; // e.g., 300 restarts

    public ExperimentRunner3(String instancesDir, int maxInstances, int maxRestarts) {
        this.instancesDir = instancesDir;
        this.maxInstances = maxInstances;
        this.maxRestarts = maxRestarts;
        this.algorithmFactories = new ArrayList<>();
    }

    public void registerAlgorithm(AlgorithmFactory factory) {
        algorithmFactories.add(factory);
    }

    public void runExperiments() throws IOException {
        File dir = new File(instancesDir);
        // For experiment 3, select a few interesting instances (e.g., those containing "chr12c")
        File[] instanceFiles = dir.listFiles((d, name) -> name.contains("chr12c"));
        if (instanceFiles == null || instanceFiles.length == 0) {
            System.out.println("No selected instance files found in directory: " + instancesDir);
            return;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("experiment3_results.csv"));
        // CSV header: Instance,Algorithm,Restart,BestSoFar,AverageSoFar
        String header = "Instance,Algorithm,Restart,BestSoFar,AverageSoFar";
        pw.println(header);
        System.out.println(header);

        int successfulInstances = 0;
        for (File file : instanceFiles) {
            String instanceName = file.getName();
            System.out.println("Processing instance: " + instanceName);
            Problem problem;
            try {
                problem = new Problem(file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Skipping instance " + instanceName + " due to error: " + e.getMessage());
                continue;
            }
            successfulInstances++;
            if (successfulInstances > maxInstances)
                break;

            for (AlgorithmFactory factory : algorithmFactories) {
                double cumulativeQuality = 0.0;
                int bestSoFar = Integer.MAX_VALUE;
                for (int restart = 1; restart <= maxRestarts; restart++) {
                    // Create a new algorithm instance for each restart.
                    Algorithm algorithm = factory.create(problem);
                    algorithm.run();
                    int finalFitness = algorithm.getBestFitness();
                    cumulativeQuality += finalFitness;
                    if (finalFitness < bestSoFar) {
                        bestSoFar = finalFitness;
                    }
                    double averageSoFar = cumulativeQuality / restart;
                    String line = instanceName + "," + factory.getName() + "," + restart + "," + bestSoFar + "," + averageSoFar;
                    System.out.println(line);
                    pw.println(line);
                }
            }
        }
        pw.close();
        System.out.println("Experiment 3 results saved to experiment3_results.csv");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.mycompany.qapsolver.ExperimentRunner3 <instancesDir>");
            System.exit(1);
        }
        String instancesDir = args[0];
        ExperimentRunner3 runner = new ExperimentRunner3(instancesDir, 2, 300);
        // Register G and S algorithms (choose one operator variant, e.g., 2-swap).
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "G-2swap"; }
            public Algorithm create(Problem problem) {
                return new MultiStartGreedyAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new TwoSwapOperator());
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "S-2swap"; }
            public Algorithm create(Problem problem) {
                return new MultiStartSteepestDescentAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new TwoSwapOperator());
            }
        });
        try {
            runner.runExperiments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
