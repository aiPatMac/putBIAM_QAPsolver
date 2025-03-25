package com.mycompany.qapsolver;

import java.util.Arrays;
import java.util.Random;

public class NearestNeighborAlgorithm extends Algorithm implements TimeLimitedAlgorithm {
    private final Random rand;

    public NearestNeighborAlgorithm(Problem problem) {
        super(problem);
        this.rand = new Random();
    }

    /**
     * Default run method. For backward compatibility, we run with a fixed short time (e.g., 100ms).
     */
    @Override
    public void run() {
        // Run for 100 milliseconds as a default if no time limit is provided.
        run(100_000_000L);
    }

    /**
     * Runs the nearest neighbor heuristic repeatedly—each time starting from a random starting point—
     * until the given time limit (in nanoseconds) is exceeded.
     * Updates the bestSolution if a candidate solution is better.
     */
    @Override
    public void run(long timeLimitNs) {
        long startTime = TimeUtil.currentTime();
        int bestFitness = Integer.MAX_VALUE;
        while (TimeUtil.currentTime() - startTime < timeLimitNs) {
            Solution candidate = buildNearestNeighborCandidate();
            int candidateFitness = evaluate(candidate);
            if (candidateFitness < bestFitness) {
                bestFitness = candidateFitness;
                bestSolution.copyFrom(candidate);
            }
            stepsCount++; // Count each candidate construction as one step.
        }
    }

    /**
     * Builds a candidate solution using the nearest neighbor heuristic.
     */
    private Solution buildNearestNeighborCandidate() {
        int n = problem.getSize();
        // Create and initialize a candidate solution.
        Solution candidate = initializeCandidate(n);
        int[] sol = candidate.getAssignment();

        // Randomly choose a starting facility and location.
        boolean[] assignedFacility = new boolean[n];
        boolean[] assignedLocation = new boolean[n];
        chooseRandomStart(n, sol, assignedFacility, assignedLocation);

        // Assign the remaining facilities iteratively.
        assignRemainingFacilities(n, sol, assignedFacility, assignedLocation);

        return candidate;
    }

    /**
     * Initializes a candidate solution with all positions set to -1.
     */
    private Solution initializeCandidate(int n) {
        Solution candidate = new Solution(n);
        int[] sol = candidate.getAssignment();
        Arrays.fill(sol, -1);
        return candidate;
    }

    /**
     * Chooses a random starting facility and a random starting location,
     * then marks them as assigned in the provided arrays.
     */
    private void chooseRandomStart(int n, int[] sol, boolean[] assignedFacility, boolean[] assignedLocation) {
        int f0 = rand.nextInt(n);
        int l0 = rand.nextInt(n);
        sol[f0] = l0;
        assignedFacility[f0] = true;
        assignedLocation[l0] = true;
    }

    /**
     * Assigns the remaining facilities to locations iteratively using the nearest neighbor rule.
     */
    private void assignRemainingFacilities(int n, int[] sol, boolean[] assignedFacility, boolean[] assignedLocation) {
        int[][] flow = problem.getFlowMatrix();
        int[][] distance = problem.getDistanceMatrix();

        // For each remaining facility.
        for (int k = 1; k < n; k++) {
            int[] bestPair = findBestAssignment(n, sol, assignedFacility, assignedLocation, flow, distance);
            int bestF = bestPair[0];
            int bestL = bestPair[1];
            sol[bestF] = bestL;
            assignedFacility[bestF] = true;
            assignedLocation[bestL] = true;
        }
    }

    /**
     * Finds the best unassigned facility and location pair that minimizes the incremental cost.
     * Returns an array where index 0 is the facility and index 1 is the location.
     */
    private int[] findBestAssignment(int n, int[] sol, boolean[] assignedFacility, boolean[] assignedLocation,
                                     int[][] flow, int[][] distance) {
        double bestCostIncrease = Double.MAX_VALUE;
        int bestF = -1;
        int bestL = -1;
        for (int f = 0; f < n; f++) {
            if (!assignedFacility[f]) {
                for (int l = 0; l < n; l++) {
                    if (!assignedLocation[l]) {
                        double costIncrease = computeCostIncrease(f, l, sol, assignedFacility, flow, distance, n);
                        if (costIncrease < bestCostIncrease) {
                            bestCostIncrease = costIncrease;
                            bestF = f;
                            bestL = l;
                        }
                    }
                }
            }
        }
        return new int[]{bestF, bestL};
    }

    /**
     * Computes the cost increase of assigning facility f to location l,
     * based on the cost contributions from all already assigned facilities.
     */
    private double computeCostIncrease(int f, int l, int[] sol, boolean[] assignedFacility,
                                       int[][] flow, int[][] distance, int n) {
        double costIncrease = 0.0;
        for (int f2 = 0; f2 < n; f2++) {
            if (assignedFacility[f2]) {
                int loc_f2 = sol[f2];
                costIncrease += flow[f][f2] * distance[l][loc_f2]
                        + flow[f2][f] * distance[loc_f2][l];
                evaluationsCount++; // Count each evaluation.
            }
        }
        return costIncrease;
    }
}
