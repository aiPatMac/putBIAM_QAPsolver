package com.mycompany.qapsolver;

import java.io.*;
import java.util.*;

public class ExperimentRunner4 {
    public interface AlgorithmFactory {
        String getName();
        Algorithm create(Problem problem);
    }

    private final List<AlgorithmFactory> algorithmFactories;
    private final String instancesDir;
    private final int runsPerInstance;
    private final int maxInstances;

    public ExperimentRunner4(String instancesDir, int runsPerInstance, int maxInstances) {
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
        // For experiment 4, select two specific instances (e.g., those containing "chr12c")
        File[] instanceFiles = dir.listFiles((d, name) -> name.contains("chr12c"));
        if (instanceFiles == null || instanceFiles.length == 0) {
            System.out.println("No selected instance files found in directory: " + instancesDir);
            return;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("experiment4_results.csv"));
        // CSV header: Instance,Algorithm,Run,FinalFitness,SimilarityToBest
        String header = "Instance,Algorithm,Run,FinalFitness,SimilarityToBest";
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
            if (successfulInstances > maxInstances) break;

            for (AlgorithmFactory factory : algorithmFactories) {
                // Collect solutions over multiple runs.
                List<Solution> solutionList = new ArrayList<>();
                List<Integer> fitnessList = new ArrayList<>();
                for (int run = 1; run <= runsPerInstance; run++) {
                    Algorithm algorithm = factory.create(problem);
                    algorithm.run();
                    int finalFitness = algorithm.getBestFitness();
                    Solution finalSolution = new Solution(problem.getSize());
                    finalSolution.copyFrom(algorithm.getBestSolution());
                    solutionList.add(finalSolution);
                    fitnessList.add(finalFitness);
                }
                // Determine the best overall solution (lowest fitness).
                int bestOverallFitness = Integer.MAX_VALUE;
                Solution bestOverallSolution = null;
                for (int i = 0; i < runsPerInstance; i++) {
                    if (fitnessList.get(i) < bestOverallFitness) {
                        bestOverallFitness = fitnessList.get(i);
                        bestOverallSolution = solutionList.get(i);
                    }
                }
                // For each run, compute similarity.
                for (int i = 0; i < runsPerInstance; i++) {
                    double similarity = solutionList.get(i).similarityTo(bestOverallSolution);
                    String line = instanceName + "," + factory.getName() + "," + (i+1) + ","
                            + fitnessList.get(i) + "," + similarity;
                    System.out.println(line);
                    pw.println(line);
                }
            }
        }
        pw.close();
        System.out.println("Experiment 4 results saved to experiment4_results.csv");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.mycompany.qapsolver.ExperimentRunner4 <instancesDir>");
            System.exit(1);
        }
        String instancesDir = args[0];
        // For experiment 4, we run 100 repetitions on 2 selected instances.
        ExperimentRunner4 runner = new ExperimentRunner4(instancesDir, 100, 2);
        // Register one or more algorithms (e.g., using the heuristic "H").
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "G"; }
            public Algorithm create(Problem problem) {
                return new MultiStartGreedyAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new TwoSwapOperator());
            }
        });
        try {
            runner.runExperiments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
