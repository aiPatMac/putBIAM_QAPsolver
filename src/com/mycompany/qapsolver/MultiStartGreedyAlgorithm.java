package com.mycompany.qapsolver;

public class MultiStartGreedyAlgorithm extends Algorithm {
    private final int maxIterations;
    private final int randomStarts;

    public MultiStartGreedyAlgorithm(Problem problem, int maxIterations, int randomStarts) {
        super(problem);
        this.maxIterations = maxIterations;
        this.randomStarts = randomStarts;
    }

    @Override
    public void run() {
        int bestOverallFitness = Integer.MAX_VALUE;
        Solution bestOverallSolution = new Solution(problem.getSize());

        for (int start = 0; start < randomStarts; start++) {
            // Initialize with a random solution.
            RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
            initializer.run();
            currentSolution.copyFrom(initializer.getBestSolution());
            int currentFitness = evaluate(currentSolution);

            // Greedy local search (first-improvement).
            boolean improved;
            for (int iter = 0; iter < maxIterations; iter++) {
                improved = false;
                for (int i = 0; i < problem.getSize() - 1; i++) {
                    for (int j = i + 1; j < problem.getSize(); j++) {
                        currentSolution.swap(i, j);
                        int newFitness = evaluate(currentSolution);
                        if (newFitness < currentFitness) {
                            currentFitness = newFitness;
                            improved = true;
                            stepsCount++; // Count each accepted improvement as a step.
                            break; // Accept first improvement.
                        } else {
                            currentSolution.swap(i, j); // Revert swap.
                        }
                    }
                    if (improved) break;
                }
                if (!improved) break;
            }

            if (currentFitness < bestOverallFitness) {
                bestOverallFitness = currentFitness;
                bestOverallSolution.copyFrom(currentSolution);
            }
        }
        bestSolution.copyFrom(bestOverallSolution);
    }
}
