package com.mycompany.qapsolver;

import java.util.Random;

public class SimulatedAnnealingAlgorithm extends Algorithm implements TimeLimitedAlgorithm {
    private final NeighborhoodOperator operator = new TwoSwapOperator();
    private final double alpha = 0.9;
    private final int P = 10;

    private long timeLimitNs;

    public SimulatedAnnealingAlgorithm(Problem problem) {
        super(problem);
    }

    @Override
    public void run(long timeLimitNs) {
        this.timeLimitNs = timeLimitNs;

        Random rand = new Random();
        recordInitial();
        int n = problem.getSize();
        double acceptanceRate = 0.95;

        // Estimate avg cost delta from 100 random neighbors
        double avgDelta = estimateAverageDelta(currentSolution, 100);
        double temperature = -avgDelta / Math.log(1 - acceptanceRate);

        int L = n * 5; // Markov chain length per temperature
        int noImprovement = 0;

        int currentFitness = evaluate(currentSolution);
        int bestFitness = currentFitness;
        bestSolution.copyFrom(currentSolution);

        long startTime = TimeUtil.currentTime();

        while ((TimeUtil.currentTime() - startTime) < timeLimitNs &&
                noImprovement < P * L &&
                temperature > 1e-3) {
            boolean improved = false;
            for (int i = 0; i < L; i++) {
                Solution neighbor = operator.getRandomNeighbor(currentSolution);
                int neighborFitness = evaluate(neighbor);
                int delta = neighborFitness - currentFitness;

                if (delta < 0 || rand.nextDouble() < Math.exp(-delta / temperature)) {
                    currentSolution.copyFrom(neighbor);
                    currentFitness = neighborFitness;
                    stepsCount++;
                    if (currentFitness < bestFitness) {
                        bestFitness = currentFitness;
                        bestSolution.copyFrom(currentSolution);
                        improved = true;
                    }
                }

                if ((TimeUtil.currentTime() - startTime) >= timeLimitNs) break;
            }

            if (!improved) noImprovement++;
            else noImprovement = 0;

            temperature *= alpha;
        }
    }

    private double estimateAverageDelta(Solution solution, int samples) {
        double sum = 0;
        for (int i = 0; i < samples; i++) {
            Solution neighbor = operator.getRandomNeighbor(solution);
            int delta = Math.abs(evaluate(neighbor) - evaluate(solution));
            sum += delta;
        }
        return sum / samples;
    }

    @Override
    public void run() {
        run(this.timeLimitNs);
    }
}
