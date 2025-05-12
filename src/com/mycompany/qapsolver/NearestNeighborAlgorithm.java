package com.mycompany.qapsolver;

import java.util.Arrays;
import java.util.Random;

public class NearestNeighborAlgorithm extends Algorithm {
    private final Random rand;

    public NearestNeighborAlgorithm(Problem problem) {
        super(problem);
        this.rand = new Random();
    }

    /**
     * Executes the Nearest Neighbor heuristic for the Quadratic Assignment Problem (QAP).
     *
     * Steps:
     * 1. Generate a random initial solution using RandomSearchAlgorithm.
     * 2. From that initial solution, fix one starting assignment (facility -> location).
     * 3. Iteratively assign remaining facilities using the Nearest Neighbor rule:
     *    - For every unassigned facility and location, calculate the cost increase.
     *    - Select the pair (facility, location) with the smallest cost increase.
     * 4. Copy the constructed solution into bestSolution.
     */
    @Override
    public void run() {
        // Generate a random solution using RandomSearchAlgorithm with 1 iteration.
        RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
        initializer.run();

        // Use the random solution as the starting point.
        currentSolution.copyFrom(initializer.getBestSolution());
        recordInitial(); // Save as the initial solution for tracking.

        int n = problem.getSize();
        int[] sol = currentSolution.getAssignment();

        // Track which facilities and locations are already assigned.
        boolean[] assignedFacility = new boolean[n];
        boolean[] assignedLocation = new boolean[n];

        // Start from facility at index 0 (could be randomized).
        int startFacility = 0;
        int startLocation = sol[startFacility];
        assignedFacility[startFacility] = true;
        assignedLocation[startLocation] = true;

        // Iteratively assign the remaining facilities.
        assignRemainingFacilities(n, sol, assignedFacility, assignedLocation);

        // Save the final constructed solution as the best found by this heuristic.
        bestSolution.copyFrom(currentSolution);
    }

    /**
     * Assigns the remaining facilities using the nearest neighbor heuristic.
     * At each step, selects the unassigned facility-location pair that causes the smallest increase in cost.
     */
    private void assignRemainingFacilities(int n, int[] sol, boolean[] assignedFacility, boolean[] assignedLocation) {
        int[][] flow = problem.getFlowMatrix();
        int[][] distance = problem.getDistanceMatrix();

        // Assign the remaining n-1 facilities.
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
     * Finds the (facility, location) pair that leads to the lowest increase in cost.
     * Considers only unassigned facilities and unassigned locations.
     */
    private int[] findBestAssignment(int n, int[] sol, boolean[] assignedFacility, boolean[] assignedLocation,
                                     int[][] flow, int[][] distance) {
        double bestCostIncrease = Double.MAX_VALUE;
        int bestF = -1, bestL = -1;

        for (int f = 0; f < n; f++) {
            if (!assignedFacility[f]) {
                for (int l = 0; l < n; l++) {
                    if (!assignedLocation[l]) {
                        // Evaluate how much cost increases if f is assigned to l.
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
     * Computes the additional cost incurred by assigning facility f to location l.
     * It sums up the cost contributions with all already assigned facilities.
     */
    private double computeCostIncrease(int f, int l, int[] sol, boolean[] assignedFacility,
                                       int[][] flow, int[][] distance, int n) {
        double costIncrease = 0.0;
        for (int f2 = 0; f2 < n; f2++) {
            if (assignedFacility[f2]) {
                int loc_f2 = sol[f2];
                // Add the cost for both directions of flow.
                costIncrease += flow[f][f2] * distance[l][loc_f2] + flow[f2][f] * distance[loc_f2][l];
                evaluationsCount++; // Count each pairwise evaluation as one operation.
            }
        }
        return costIncrease;
    }
}
