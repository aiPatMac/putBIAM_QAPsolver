package com.mycompany.qapsolver;

import java.io.*;
import java.util.*;

public class ExperimentRunner {
    public interface AlgorithmFactory {
        String getName();
        Algorithm create(Problem problem);
    }

    private final List<AlgorithmFactory> algorithmFactories;
    private final String instancesDir;
    private final int runsPerInstance;
    private final int maxInstances;

    public ExperimentRunner(String instancesDir, int runsPerInstance, int maxInstances) {
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
        File[] instanceFiles = dir.listFiles((d, name) -> name.endsWith("chr12c.dat"));
        if (instanceFiles == null || instanceFiles.length == 0) {
            System.out.println("No instance files found in directory: " + instancesDir);
            return;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("experiment_results.csv"));
        // CSV header includes initial and final solution data.
        String csvHeader = "Instance,Algorithm,Run,InitialFitness,InitialSolution,FinalFitness,FinalSolution,TimeMs,Evaluations,Steps";
        pw.println(csvHeader);
        System.out.println(csvHeader);

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
            if (successfulInstances > maxInstances) {
                break;
            }

            // Estimate time budget range using parameters for G and S.
            TimeBudgetRange timeRange = ExperimentRunnerHelper.estimateTimeBudgetRangeAll(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS);
            System.out.println("Estimated time budget range (ns) for RS/RW/H: " + timeRange.minTime + " to " + timeRange.maxTime);

            for (AlgorithmFactory factory : algorithmFactories) {
                for (int run = 1; run <= runsPerInstance; run++) {
                    long startTime = TimeUtil.currentTime();
                    Algorithm algorithm = factory.create(problem);

                    // For RS, RW, or H, run dynamically if applicable.
                    if (factory.getName().equals("RS") || factory.getName().equals("RW") || factory.getName().equals("H")) {
                        if (algorithm instanceof TimeLimitedAlgorithm) {
                            long timeBudget = timeRange.randomBudget();
                            ((TimeLimitedAlgorithm) algorithm).run(timeBudget);
                        } else {
                            algorithm.run();
                        }
                    } else {
                        algorithm.run();
                    }

                    long elapsedNs = TimeUtil.currentTime() - startTime;
                    double elapsedMs = elapsedNs / 1_000_000.0;
                    int finalFitness = algorithm.getBestFitness();
                    long evaluations = algorithm.getEvaluationsCount();
                    long steps = algorithm.getStepsCount();

                    String initSolStr = algorithm.getInitialSolution() != null ? algorithm.getInitialSolution().toString() : "NA";
                    String line = instanceName + "," + factory.getName() + "," + run + ","
                            + algorithm.getInitialFitness() + "," + initSolStr + ","
                            + finalFitness + "," + algorithm.getBestSolution().toString() + ","
                            + elapsedMs + "," + evaluations + "," + steps;
                    System.out.println(line);
                    pw.println(line);
                }
            }
        }
        pw.close();
        System.out.println("Experiment results saved to experiment_results.csv");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.mycompany.qapsolver.ExperimentRunner <instancesDir>");
            System.exit(1);
        }
        String instancesDir = args[0];
        ExperimentRunner runner = new ExperimentRunner(instancesDir, Config.RUNS_PER_INSTANCE, Config.MAX_INSTANCES);

        // Register algorithms.
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "RS"; }
            public Algorithm create(Problem problem) {
                return new RandomSearchAlgorithm(problem, Config.RS_ITERATIONS);
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "RW"; }
            public Algorithm create(Problem problem) {
                return new RandomWalkAlgorithm(problem, Config.RW_ITERATIONS);
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "H"; }
            public Algorithm create(Problem problem) {
                return new NearestNeighborAlgorithm(problem);
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "G-2swap"; }
            public Algorithm create(Problem problem) {
                return new MultiStartGreedyAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new TwoSwapOperator());
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "G-3opt"; }
            public Algorithm create(Problem problem) {
                return new MultiStartGreedyAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new ThreeOptOperator());
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "S-2swap"; }
            public Algorithm create(Problem problem) {
                return new MultiStartSteepestDescentAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new TwoSwapOperator());
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "S-3opt"; }
            public Algorithm create(Problem problem) {
                return new MultiStartSteepestDescentAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new ThreeOptOperator());
            }
        });

        try {
            runner.runExperiments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
