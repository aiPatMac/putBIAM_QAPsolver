package com.mycompany.qapsolver;

public abstract class Algorithm {
    protected final Problem problem;
    protected final Solution currentSolution;
    protected final Solution bestSolution;

    // Counters for metrics.
    protected long evaluationsCount = 0;
    protected long stepsCount = 0;

    // Fields to record the initial solution and its fitness.
    protected Solution initialSolution;
    protected int initialFitness;

    public Algorithm(Problem problem) {
        this.problem = problem;
        int size = problem.getSize();
        this.currentSolution = new Solution(size);
        this.bestSolution = new Solution(size);
        // Initially, copy currentSolution into bestSolution.
        this.bestSolution.copyFrom(currentSolution);
    }

    /**
     * Records the current state of currentSolution as the initial solution,
     * along with its evaluated fitness.
     */
    protected void recordInitial() {
        this.initialSolution = new Solution(problem.getSize());
        this.initialSolution.copyFrom(currentSolution);
        this.initialFitness = evaluate(currentSolution);
    }

    public Solution getInitialSolution() {
        return initialSolution;
    }

    public int getInitialFitness() {
        return initialFitness;
    }

    /**
     * Evaluates the fitness (cost) of a given solution.
     * Each call to evaluate() increments evaluationsCount.
     */
    protected int evaluate(Solution sol) {
        evaluationsCount++;
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
