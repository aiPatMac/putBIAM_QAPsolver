package com.mycompany.qapsolver;

public class MultiStartGreedyAlgorithm extends LocalSearchAlgorithm {

    public MultiStartGreedyAlgorithm(Problem problem, int maxIterations, int randomStarts, NeighborhoodOperator operator) {
        super(problem, maxIterations, randomStarts);
        this.operator = operator;
    }

    private final NeighborhoodOperator operator;

    @Override
    public void run() {
        int bestOverallFitness = Integer.MAX_VALUE;
        Solution bestOverallSolution = new Solution(problem.getSize());
        int n = problem.getSize();
        for (int start = 0; start < randomStarts; start++) {
            // Initialize with a random solution.
            RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
            initializer.run();
            currentSolution.copyFrom(initializer.getBestSolution());
            // Record initial solution for this start.
            recordInitial();
            int currentFitness = evaluate(currentSolution);
            // Greedy first-improvement using the operator.
            for (int iter = 0; iter < maxIterations; iter++) {
                Solution neighbor = operator.getRandomNeighbor(currentSolution);
                int newFitness = evaluate(neighbor);
                if (newFitness < currentFitness) {
                    currentFitness = newFitness;
                    currentSolution.copyFrom(neighbor);
                    stepsCount++;
                    break; // Accept first improvement.
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
        // Not used since run() is overridden.
        return currentFitness;
    }
}
