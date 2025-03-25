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
     * Runs the nearest neighbor heuristic once.
     * It first obtains a random starting solution (via RandomSearchAlgorithm),
     * records that as the initial solution, and then performs the nearest neighbor improvement.
     */
    @Override
    public void run() {
        // Use RandomSearchAlgorithm to generate a random starting solution.
        RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
        initializer.run();
        // Copy the random solution as the starting point.
        currentSolution.copyFrom(initializer.getBestSolution());
        // Record the initial solution and fitness.
        recordInitial();

        // Now, build the candidate using the nearest neighbor improvement.
        int n = problem.getSize();
        int[] sol = currentSolution.getAssignment();

        // Create new boolean arrays to track which facility and location are already assigned.
        boolean[] assignedFacility = new boolean[n];
        boolean[] assignedLocation = new boolean[n];

        // Use the random starting solution: for instance, take the facility at position 0 of currentSolution.
        // (You may choose a different rule if desired.)
        int startFacility = 0;
        int startLocation = sol[startFacility];
        assignedFacility[startFacility] = true;
        assignedLocation[startLocation] = true;

        // Apply the nearest neighbor rule to assign all remaining facilities.
        assignRemainingFacilities(n, sol, assignedFacility, assignedLocation);

        // Save the improved solution.
        bestSolution.copyFrom(currentSolution);
    }

    /**
     * Initializes a candidate solution with all positions set to -1.
     */
    private Solution initializeCandidate(int n) {
        Solution candidate = new Solution(n);
        Arrays.fill(candidate.getAssignment(), -1);
        return candidate;
    }

    /**
     * Assigns the remaining facilities iteratively using the nearest neighbor rule.
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
        int bestF = -1, bestL = -1;
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
                costIncrease += flow[f][f2] * distance[l][loc_f2] + flow[f2][f] * distance[loc_f2][l];
                evaluationsCount++; // Count each evaluation.
            }
        }
        return costIncrease;
    }
}
