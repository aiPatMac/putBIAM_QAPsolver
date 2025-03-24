package com.mycompany.qapsolver;

public class MultiStartSteepestDescentAlgorithm extends Algorithm {
    private final int maxIterations;
    private final int randomStarts;

    public MultiStartSteepestDescentAlgorithm(Problem problem, int maxIterations, int randomStarts) {
        super(problem);
        this.maxIterations = maxIterations;
        this.randomStarts = randomStarts;
    }

    @Override
    public void run() {
        int bestOverallFitness = Integer.MAX_VALUE;
        Solution bestOverallSolution = new Solution(problem.getSize());
        int n = problem.getSize();

        for (int start = 0; start < randomStarts; start++) {
            RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
            initializer.run();
            currentSolution.copyFrom(initializer.getBestSolution());
            int currentFitness = evaluate(currentSolution);

            for (int iter = 0; iter < maxIterations; iter++) {
                int bestNeighborFitness = currentFitness;
                int bestI = -1, bestJ = -1;
                for (int i = 0; i < n - 1; i++) {
                    for (int j = i + 1; j < n; j++) {
                        currentSolution.swap(i, j);
                        int newFitness = evaluate(currentSolution);
                        if (newFitness < bestNeighborFitness) {
                            bestNeighborFitness = newFitness;
                            bestI = i;
                            bestJ = j;
                        }
                        currentSolution.swap(i, j);
                    }
                }
                if (bestNeighborFitness < currentFitness) {
                    currentSolution.swap(bestI, bestJ);
                    currentFitness = bestNeighborFitness;
                    stepsCount++; // Count this accepted move as a step.
                } else {
                    break;
                }
            }

            if (currentFitness < bestOverallFitness) {
                bestOverallFitness = currentFitness;
                bestOverallSolution.copyFrom(currentSolution);
            }
        }
        bestSolution.copyFrom(bestOverallSolution);
    }
}
