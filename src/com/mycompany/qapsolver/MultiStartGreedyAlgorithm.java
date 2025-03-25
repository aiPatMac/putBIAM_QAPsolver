package com.mycompany.qapsolver;

import java.util.List;
import java.util.Collections;

public class MultiStartGreedyAlgorithm extends OperatorLocalSearchAlgorithm {

    public MultiStartGreedyAlgorithm(Problem problem, int maxIterations, int randomStarts, NeighborhoodOperator operator) {
        super(problem, maxIterations, randomStarts, operator);
    }

    @Override
    protected int localSearch(int currentFitness, int n) {
        // Greedy first-improvement using the operator.
        for (int iter = 0; iter < maxIterations; iter++) {
            // Get one random neighbor.
            Solution neighbor = operator.getRandomNeighbor(currentSolution);
            int newFitness = evaluate(neighbor);
            if (newFitness < currentFitness) {
                currentFitness = newFitness;
                currentSolution.copyFrom(neighbor);
                stepsCount++;
                // Accept first improvement and then start the next iteration.
            } else {
                // No improvement found in this random check; continue trying.
            }
        }
        return currentFitness;
    }
}
