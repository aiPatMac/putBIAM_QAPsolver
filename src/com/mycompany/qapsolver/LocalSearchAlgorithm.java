package com.mycompany.qapsolver;

public abstract class LocalSearchAlgorithm extends Algorithm {
    protected final int maxIterations;
    protected final int randomStarts;

    public LocalSearchAlgorithm(Problem problem, int maxIterations, int randomStarts) {
        super(problem);
        this.maxIterations = maxIterations;
        this.randomStarts = randomStarts;
    }

    /**
     * The run method implements the multi‑start framework.
     * For each random start, it initializes a solution and improves it using localSearch().
     * Finally, it keeps the best overall solution.
     */
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
            int currentFitness = evaluate(currentSolution);

            // Improve the current solution using a specific local search method.
            currentFitness = localSearch(currentFitness, n);

            // Update overall best solution if this run produced an improvement.
            if (currentFitness < bestOverallFitness) {
                bestOverallFitness = currentFitness;
                bestOverallSolution.copyFrom(currentSolution);
            }
        }
        bestSolution.copyFrom(bestOverallSolution);
    }

    /**
     * Abstract method for performing the local search improvement from a given starting solution.
     * This method should update currentSolution in-place and return its final fitness.
     *
     * @param currentFitness the fitness of the starting solution.
     * @param n              the problem size.
     * @return the improved fitness after local search.
     */
    protected abstract int localSearch(int currentFitness, int n);
}
