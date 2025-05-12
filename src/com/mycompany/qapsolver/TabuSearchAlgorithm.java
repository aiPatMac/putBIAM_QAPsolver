package com.mycompany.qapsolver;

import java.util.*;

public class TabuSearchAlgorithm extends Algorithm implements TimeLimitedAlgorithm {
    private final NeighborhoodOperator operator = new TwoSwapOperator();
    private long timeLimitNs;
    private final int tabuTenureDivisor = 4;
    private final double candidateFraction = 0.2;

    public TabuSearchAlgorithm(Problem problem) {
        super(problem);
    }

    @Override
    public void run(long timeLimitNs) {
        this.timeLimitNs = timeLimitNs;
        recordInitial();

        int n = problem.getSize();
        int tabuTenure = Math.max(1, n / tabuTenureDivisor);
        int maxTabuSize = n * n;

        Map<String, Integer> tabuList = new HashMap<>();
        long startTime = TimeUtil.currentTime();

        int bestFitness = evaluate(currentSolution);
        bestSolution.copyFrom(currentSolution);
        int noImprovement = 0;

        while ((TimeUtil.currentTime() - startTime) < timeLimitNs && noImprovement < n * 10) {
            List<Solution> neighbors = operator.generateNeighbors(currentSolution);
            Collections.shuffle(neighbors);
            int sampleSize = Math.max(1, (int) (candidateFraction * neighbors.size()));
            List<Solution> candidates = neighbors.subList(0, sampleSize);

            Solution bestCandidate = null;
            int bestCandidateFitness = Integer.MAX_VALUE;
            String bestMoveKey = null;

            for (Solution candidate : candidates) {
                int fitness = evaluate(candidate);
                String moveKey = moveKey(currentSolution, candidate);

                boolean isTabu = tabuList.containsKey(moveKey);
                boolean aspiration = fitness < evaluate(bestSolution);

                if ((!isTabu || aspiration) && fitness < bestCandidateFitness) {
                    bestCandidate = candidate;
                    bestCandidateFitness = fitness;
                    bestMoveKey = moveKey;
                }
            }

            if (bestCandidate != null) {
                currentSolution.copyFrom(bestCandidate);
                stepsCount++;

                // Add to tabu list
                tabuList.put(bestMoveKey, tabuTenure);

                // Decay all tabu entries
                tabuList.entrySet().removeIf(e -> e.setValue(e.getValue() - 1) <= 0);

                if (bestCandidateFitness < bestFitness) {
                    bestFitness = bestCandidateFitness;
                    bestSolution.copyFrom(bestCandidate);
                    noImprovement = 0;
                } else {
                    noImprovement++;
                }
            } else {
                break; // No valid move found
            }
        }
    }

    private String moveKey(Solution from, Solution to) {
        int[] a = from.getAssignment();
        int[] b = to.getAssignment();
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return i + "-" + b[i];
        }
        return "no-change";
    }

    @Override
    public void run() {
        run(this.timeLimitNs);
    }
}
