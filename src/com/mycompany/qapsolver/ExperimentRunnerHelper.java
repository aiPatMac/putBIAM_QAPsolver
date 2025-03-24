package com.mycompany.qapsolver;


public class ExperimentRunnerHelper {
    public static TimeBudgetRange estimateTimeBudgetRange(Problem problem, int maxIterations, int randomStarts) {
        // Time a run of the MultiStartGreedyAlgorithm (G)
        long startG = TimeUtil.currentTime();
        Algorithm greedy = new MultiStartGreedyAlgorithm(problem, maxIterations, randomStarts);
        greedy.run();
        long timeG = TimeUtil.currentTime() - startG;

        // Time a run of the MultiStartSteepestDescentAlgorithm (S)
        long startS = TimeUtil.currentTime();
        Algorithm steepest = new MultiStartSteepestDescentAlgorithm(problem, maxIterations, randomStarts);
        steepest.run();
        long timeS = TimeUtil.currentTime() - startS;

        long min = Math.min(timeG, timeS);
        long max = Math.max(timeG, timeS);
        return new TimeBudgetRange(min, max);
    }
}
