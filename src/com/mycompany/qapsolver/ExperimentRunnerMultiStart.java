package com.mycompany.qapsolver;

import java.io.*;
import java.util.*;

public class ExperimentRunnerMultiStart {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java com.mycompany.qapsolver.ExperimentRunnerMultiStart <instancesDir>");
            System.exit(1);
        }

        String instancesDir = args[0];
        Set<String> allowedInstances = ExperimentRunner.getAllowedInstanceNames("optimal_data.csv");

        File[] instanceFiles = new File(instancesDir).listFiles((dir, name) ->
                allowedInstances.contains(name.toLowerCase()) && name.endsWith(".dat")
        );

        if (instanceFiles == null || instanceFiles.length == 0) {
            System.out.println("No valid instance files found.");
            return;
        }

        // Choose first 3 only
        Arrays.sort(instanceFiles, Comparator.comparing(File::getName));
        int maxInstances = Math.min(3, instanceFiles.length);

        try (PrintWriter writer = new PrintWriter(new FileWriter("experiment_results_multistart.csv"))) {
            writer.println("Instance,Algorithm,RandomStarts,Run,FinalFitness");

            for (int i = 0; i < maxInstances; i++) {
                File instanceFile = instanceFiles[i];
                String instanceName = instanceFile.getName();
                Problem problem = new Problem(instanceFile.getAbsolutePath());

                for (int restarts = 1; restarts <= 300; restarts++) {
                    for (int run = 1; run <= 5; run++) {
                        System.out.println("Running instance: " + instanceName + ", Restarts: " + restarts + ", Run: " + run);
                        // Greedy
                        Algorithm greedy = new MultiStartGreedyAlgorithm(problem, Config.GS_MAX_ITERATIONS, restarts, new TwoSwapOperator());
                        greedy.run();
                        writer.printf("%s,%s,%d,%d,%d%n", instanceName, "G-2swap", restarts, run, greedy.getBestFitness());

                        // Steepest
                        Algorithm steepest = new MultiStartSteepestDescentAlgorithm(problem, Config.GS_MAX_ITERATIONS, restarts, new TwoSwapOperator());
                        steepest.run();
                        writer.printf("%s,%s,%d,%d,%d%n", instanceName, "S-2swap", restarts, run, steepest.getBestFitness());
                    }
                }
            }

            System.out.println("Multi-start experiment results saved to experiment_results_multistart.csv");
        }
    }
}
