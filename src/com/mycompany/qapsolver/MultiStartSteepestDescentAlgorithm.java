package com.mycompany.qapsolver;

import java.util.List;

public class MultiStartSteepestDescentAlgorithm extends OperatorLocalSearchAlgorithm {

    public MultiStartSteepestDescentAlgorithm(Problem problem, int maxIterations, int randomStarts, NeighborhoodOperator operator) {
        super(problem, maxIterations, randomStarts, operator);
    }

    @Override
    protected int localSearch(int currentFitness, int n) {
        for (int iter = 0; iter < maxIterations; iter++) {
            int bestNeighborFitness = currentFitness;
            Solution bestNeighbor = null;
            // Enumerate all neighbors via the operator.
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
                break; // No improving neighbor found.
            }
        }
        return currentFitness;
    }
}
