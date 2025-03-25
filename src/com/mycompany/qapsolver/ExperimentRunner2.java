package com.mycompany.qapsolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ExperimentRunner2 {
    public interface AlgorithmFactory {
        String getName();
        Algorithm create(Problem problem);
    }

    private final List<AlgorithmFactory> algorithmFactories;
    private final String instancesDir;
    private final int runsPerInstance;
    private final int maxInstances;

    public ExperimentRunner2(String instancesDir, int runsPerInstance, int maxInstances) {
        this.instancesDir = instancesDir;
        this.runsPerInstance = runsPerInstance;
        this.maxInstances = maxInstances;
        this.algorithmFactories = new ArrayList<>();
    }

    public void registerAlgorithm(AlgorithmFactory factory) {
        algorithmFactories.add(factory);
    }

    public void runExperiments() throws IOException {
        File dir = new File(instancesDir);
        File[] instanceFiles = dir.listFiles((d, name) -> name.endsWith(".dat"));
        if (instanceFiles == null || instanceFiles.length == 0) {
            System.out.println("No instance files found in directory: " + instancesDir);
            return;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("experiment2_results.csv"));
        // CSV header: Instance,Algorithm,Run,InitialFitness,FinalFitness
        String header = "Instance,Algorithm,Run,InitialFitness,FinalFitness";
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
                for (int run = 1; run <= runsPerInstance; run++) {
                    Algorithm algorithm = factory.create(problem);
                    algorithm.run();
                    int initFitness = algorithm.getInitialFitness();
                    int finalFitness = algorithm.getBestFitness();
                    String line = instanceName + "," + factory.getName() + "," + run + "," +
                            initFitness + "," + finalFitness;
                    System.out.println(line);
                    pw.println(line);
                }
            }
        }
        pw.close();
        System.out.println("Experiment 2 results saved to experiment2_results.csv");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.mycompany.qapsolver.ExperimentRunner2 <instancesDir>");
            System.exit(1);
        }
        String instancesDir = args[0];
        // For Experiment 2, we run 200 repetitions per instance.
        ExperimentRunner2 runner = new ExperimentRunner2(instancesDir, 200, 10);

        // Register only the G and S algorithms.
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
