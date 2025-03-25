package com.mycompany.qapsolver;

import java.util.List;

public class MultiStartSteepestDescentAlgorithm extends LocalSearchAlgorithm {
    private final NeighborhoodOperator operator;

    public MultiStartSteepestDescentAlgorithm(Problem problem, int maxIterations, int randomStarts, NeighborhoodOperator operator) {
        super(problem, maxIterations, randomStarts);
        this.operator = operator;
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
            recordInitial();
            int currentFitness = evaluate(currentSolution);
            for (int iter = 0; iter < maxIterations; iter++) {
                int bestNeighborFitness = currentFitness;
                Solution bestNeighbor = null;
                List<Solution> neighbors = operator.generateNeighbors(currentSolution);
                for (Solution neighbor : neighbors) {
                    int newFitness = evaluate(neighbor);
                    if (newFitness < bestNeighborFitness) {
                        bestNeighborFitness = newFitness;
                        bestNeighbor = neighbor;
                    }
                }
                if (bestNeighbor != null && bestNeighborFitness < currentFitness) {
                    currentSolution.copyFrom(bestNeighbor);
                    currentFitness = bestNeighborFitness;
                    stepsCount++;
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

    @Override
    protected int localSearch(int currentFitness, int n) {
        return currentFitness;
    }
}
