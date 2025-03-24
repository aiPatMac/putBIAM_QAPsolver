package com.mycompany.qapsolver;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.mycompany.qapsolver.Main <instanceFile>");
            System.exit(1);
        }

        String instanceFile = args[0];

        try {
            // Load the instance (e.g., "bur26a.dat")
            Problem problem = new Problem(instanceFile);
            // Uncomment the next line for debugging:
            // problem.printInstance();

            // --- Run Random Search Algorithm ---
            System.out.println("Running Random Search Algorithm...");
            Algorithm randomAlgorithm = new RandomSearchAlgorithm(problem, 100000);
            randomAlgorithm.run();
            System.out.println("Random Search - Best Solution: " + randomAlgorithm.getBestSolution());
            System.out.println("Random Search - Best Fitness: " + randomAlgorithm.getBestFitness());
            System.out.println();

            // --- Run Local Search Algorithm ---
            System.out.println("Running Local Search Algorithm...");
            Algorithm localAlgorithm = new LocalSearchAlgorithm(problem, 10000000);
            localAlgorithm.run();
            System.out.println("Local Search - Best Solution: " + localAlgorithm.getBestSolution());
            System.out.println("Local Search - Best Fitness: " + localAlgorithm.getBestFitness());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
