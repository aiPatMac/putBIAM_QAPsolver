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

    public static Set<String> getAllowedInstanceNames(String optimalCsvPath) throws IOException {
        Set<String> allowed = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(optimalCsvPath))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    String name = parts[0].trim().toLowerCase();
                    if (!name.isEmpty()) {
                        allowed.add(name + ".dat");
                    }
                }
            }
        }
        return allowed;
    }

    public void runExperiments() throws IOException {
        File dir = new File(instancesDir);
        Set<String> allowedFiles = getAllowedInstanceNames("optimal_data.csv");

        File[] instanceFiles = dir.listFiles((d, name) ->
                allowedFiles.contains(name.toLowerCase())
        );

        if (instanceFiles == null || instanceFiles.length == 0) {
            System.out.println("No instance files found in directory: " + instancesDir);
            return;
        }

        PrintWriter pw = new PrintWriter(new FileWriter("experiment_results.csv"));
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
            if (successfulInstances > maxInstances) break;

            // Estimate time budget ONCE per instance
            TimeBudgetRange timeRange = ExperimentRunnerHelper.estimateTimeBudgetRangeAll(
                    problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS);
            long baseTime = timeRange.maxTime;

            for (AlgorithmFactory factory : algorithmFactories) {
                for (int run = 1; run <= runsPerInstance; run++) {
                    long startTime = TimeUtil.currentTime();
                    Algorithm algorithm = factory.create(problem);

                    String name = factory.getName();

                    if ((name.equals("SA") || name.equals("TS")) && algorithm instanceof TimeLimitedAlgorithm) {
                        ((TimeLimitedAlgorithm) algorithm).run(2 * baseTime);
                    } else if (name.equals("RS") || name.equals("RW") || name.equals("H")) {
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
                    String line = instanceName + "," + name + "," + run + "," +
                            algorithm.getInitialFitness() + ",\"" + initSolStr + "\"," +
                            finalFitness + ",\"" + algorithm.getBestSolution().toString() + "\"," +
                            elapsedMs + "," + evaluations + "," + steps;
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

        // Register algorithms
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
            public String getName() { return "S-2swap"; }
            public Algorithm create(Problem problem) {
                return new MultiStartSteepestDescentAlgorithm(problem, Config.GS_MAX_ITERATIONS, Config.GS_RANDOM_STARTS, new TwoSwapOperator());
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "SA"; }
            public Algorithm create(Problem problem) {
                return new SimulatedAnnealingAlgorithm(problem);
            }
        });
        runner.registerAlgorithm(new AlgorithmFactory() {
            public String getName() { return "TS"; }
            public Algorithm create(Problem problem) {
                return new TabuSearchAlgorithm(problem);
            }
        });

        try {
            runner.runExperiments();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
