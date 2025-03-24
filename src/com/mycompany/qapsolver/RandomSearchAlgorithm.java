package com.mycompany.qapsolver;

import java.util.Random;

public class RandomSearchAlgorithm extends Algorithm implements TimeLimitedAlgorithm {
    private final int fixedIterations; // fallback if time limit is not used
    private final Random rand;

    public RandomSearchAlgorithm(Problem problem, int iterations) {
        super(problem);
        this.fixedIterations = iterations;
        this.rand = new Random();
    }

    // The original run() method for fixed iterations.
    @Override
    public void run() {
        int bestFitness = evaluate(bestSolution);
        for (int i = 0; i < fixedIterations; i++) {
            shuffleSolution(currentSolution);
            int currentFitness = evaluate(currentSolution);
            if (currentFitness < bestFitness) {
                bestFitness = currentFitness;
                bestSolution.copyFrom(currentSolution);
            }
        }
    }

    // New time-limited run method.
    @Override
    public void run(long timeLimitNs) {
        long start = TimeUtil.currentTime();
        int bestFitness = evaluate(bestSolution);
        while (TimeUtil.currentTime() - start < timeLimitNs) {
            shuffleSolution(currentSolution);
            int currentFitness = evaluate(currentSolution);
            if (currentFitness < bestFitness) {
                bestFitness = currentFitness;
                bestSolution.copyFrom(currentSolution);
            }
        }
    }

    // Fisher–Yates shuffle
    private void shuffleSolution(Solution sol) {
        int[] arr = sol.getAssignment();
        int n = arr.length;
        for (int i = n - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
