package com.mycompany.qapsolver;

import java.util.Random;

public class RandomWalkAlgorithm extends Algorithm implements TimeLimitedAlgorithm {
    private final int fixedIterations; // fallback if time limit is not used
    private final Random rand;

    public RandomWalkAlgorithm(Problem problem, int iterations) {
        super(problem);
        this.fixedIterations = iterations;
        this.rand = new Random();
    }

    @Override
    public void run() {
        // Fixed iterations version (for backward compatibility)
        RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
        initializer.run();
        currentSolution.copyFrom(initializer.getBestSolution());
        bestSolution.copyFrom(currentSolution);

        int currentFitness = evaluate(currentSolution);
        int bestFitness = currentFitness;

        for (int i = 0; i < fixedIterations; i++) {
            int a = rand.nextInt(problem.getSize());
            int b = rand.nextInt(problem.getSize());
            while (b == a) {
                b = rand.nextInt(problem.getSize());
            }
            currentSolution.swap(a, b);
            int newFitness = evaluate(currentSolution);
            if (newFitness < currentFitness) {
                currentFitness = newFitness;
                if (newFitness < bestFitness) {
                    bestFitness = newFitness;
                    bestSolution.copyFrom(currentSolution);
                }
                stepsCount++;  // Count accepted swap as a step.
            } else {
                currentSolution.swap(a, b); // Revert swap.
            }
        }
    }

    @Override
    public void run(long timeLimitNs) {
        // Time-limited run for RW.
        RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
        initializer.run();
        currentSolution.copyFrom(initializer.getBestSolution());
        bestSolution.copyFrom(currentSolution);

        int currentFitness = evaluate(currentSolution);
        int bestFitness = currentFitness;
        long start = TimeUtil.currentTime();

        while (TimeUtil.currentTime() - start < timeLimitNs) {
            int a = rand.nextInt(problem.getSize());
            int b = rand.nextInt(problem.getSize());
            while (b == a) {
                b = rand.nextInt(problem.getSize());
            }
            currentSolution.swap(a, b);
            int newFitness = evaluate(currentSolution);
            if (newFitness < currentFitness) {
                currentFitness = newFitness;
                if (newFitness < bestFitness) {
                    bestFitness = newFitness;
                    bestSolution.copyFrom(currentSolution);
                }
                stepsCount++;  // Count accepted swap as a step.
            } else {
                currentSolution.swap(a, b); // Revert swap.
            }
        }
    }
}
