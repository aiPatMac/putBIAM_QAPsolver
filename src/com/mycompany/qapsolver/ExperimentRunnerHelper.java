package com.mycompany.qapsolver;

public class ExperimentRunnerHelper {

    /**
     * Estimates a global time budget range (in nanoseconds) for RS/RW/H runs by running all operator variants.
     * This method runs:
     *  - MultiStartGreedyAlgorithm and MultiStartSteepestDescentAlgorithm with a TwoSwapOperator,
     *  - MultiStartGreedyAlgorithm and MultiStartSteepestDescentAlgorithm with a ThreeOptOperator.
     * It then returns a TimeBudgetRange covering the overall minimum and maximum times observed.
     *
     * @param problem       the problem instance
     * @param maxIterations maximum iterations for G and S
     * @param randomStarts  number of random starts for G and S
     * @return a TimeBudgetRange with global min and max times (in nanoseconds)
     */
    public static TimeBudgetRange estimateTimeBudgetRangeAll(Problem problem, int maxIterations, int randomStarts) {
        long globalMin = Long.MAX_VALUE;
        long globalMax = Long.MIN_VALUE;

        // Array of neighborhood operators to test.
        NeighborhoodOperator[] operators = { new TwoSwapOperator() };

        // For each operator variant, run both MultiStartGreedyAlgorithm and MultiStartSteepestDescentAlgorithm.
        for (NeighborhoodOperator op : operators) {
            // Test MultiStartGreedyAlgorithm with operator op.
            long startG = TimeUtil.currentTime();
            Algorithm greedy = new MultiStartGreedyAlgorithm(problem, maxIterations, randomStarts, op);
            greedy.run();
            long timeG = TimeUtil.currentTime() - startG;

            // Test MultiStartSteepestDescentAlgorithm with operator op.
            long startS = TimeUtil.currentTime();
            Algorithm steepest = new MultiStartSteepestDescentAlgorithm(problem, maxIterations, randomStarts, op);
            steepest.run();
            long timeS = TimeUtil.currentTime() - startS;

            long currentMin = Math.min(timeG, timeS);
            long currentMax = Math.max(timeG, timeS);

            if (currentMin < globalMin) {
                globalMin = currentMin;
            }
            if (currentMax > globalMax) {
                globalMax = currentMax;
            }
        }
        return new TimeBudgetRange(globalMin, globalMax);
    }
}
