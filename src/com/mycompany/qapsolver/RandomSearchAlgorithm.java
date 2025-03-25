package com.mycompany.qapsolver;

import java.util.Random;

public class RandomSearchAlgorithm extends Algorithm implements TimeLimitedAlgorithm {
    private final int fixedIterations;
    private final Random rand;

    public RandomSearchAlgorithm(Problem problem, int iterations) {
        super(problem);
        this.fixedIterations = iterations;
        this.rand = new Random();
    }

    @Override
    public void run() {
        // Generate one random solution and record it as initial.
        shuffleSolution(currentSolution);
        recordInitial();  // Save the initial random solution.
        int bestFitness = evaluate(bestSolution);
        for (int i = 0; i < fixedIterations; i++) {
            shuffleSolution(currentSolution);
            int currentFitness = evaluate(currentSolution);
            if (currentFitness < bestFitness) {
                bestFitness = currentFitness;
                bestSolution.copyFrom(currentSolution);
                stepsCount++; // Count accepted improvement.
            }
        }
    }

    @Override
    public void run(long timeLimitNs) {
        long start = TimeUtil.currentTime();
        // Generate one random solution and record as initial.
        shuffleSolution(currentSolution);
        recordInitial();
        int bestFitness = evaluate(bestSolution);
        while (TimeUtil.currentTime() - start < timeLimitNs) {
            shuffleSolution(currentSolution);
            int currentFitness = evaluate(currentSolution);
            if (currentFitness < bestFitness) {
                bestFitness = currentFitness;
                bestSolution.copyFrom(currentSolution);
                stepsCount++;
            }
        }
    }

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
