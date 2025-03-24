package com.mycompany.qapsolver;

public abstract class Algorithm {
    protected final Problem problem;
    protected final Solution currentSolution;
    protected final Solution bestSolution;

    // Counters for metrics.
    protected long evaluationsCount = 0;
    protected long stepsCount = 0;

    public Algorithm(Problem problem) {
        this.problem = problem;
        int size = problem.getSize();
        this.currentSolution = new Solution(size);
        this.bestSolution = new Solution(size);
        // Initialize bestSolution with the starting solution.
        this.bestSolution.copyFrom(currentSolution);
    }

    /**
     * Evaluates the fitness (cost) of a given solution.
     * Each call to evaluate() is counted as one full evaluation.
     */
    protected int evaluate(Solution sol) {
        evaluationsCount++;  // Increment counter for each full evaluation.
        int[][] flow = problem.getFlowMatrix();
        int[][] distance = problem.getDistanceMatrix();
        int[] assignment = sol.getAssignment();
        int size = problem.getSize();
        int cost = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cost += flow[i][j] * distance[assignment[i]][assignment[j]];
            }
        }
        return cost;
    }

    // Each algorithm must implement its own run() method.
    public abstract void run();

    public Solution getBestSolution() {
        return bestSolution;
    }

    public int getBestFitness() {
        return evaluate(bestSolution);
    }

    public long getEvaluationsCount() {
        return evaluationsCount;
    }

    public long getStepsCount() {
        return stepsCount;
    }
}
