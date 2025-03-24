package com.mycompany.qapsolver;

import java.util.Arrays;

public class NearestNeighborAlgorithm extends Algorithm {

    public NearestNeighborAlgorithm(Problem problem) {
        super(problem);
    }

    @Override
    public void run() {
        int n = problem.getSize();
        int[] sol = currentSolution.getAssignment();
        // Mark unassigned positions with -1.
        Arrays.fill(sol, -1);
        boolean[] assignedFacility = new boolean[n];
        boolean[] assignedLocation = new boolean[n];

        // Choose an initial facility (e.g., facility 0) and assign it to the location with the smallest total distance.
        int f0 = 0;
        int bestLoc = 0;
        int bestLocSum = Integer.MAX_VALUE;
        int[][] distance = problem.getDistanceMatrix();
        for (int l = 0; l < n; l++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += distance[l][j];
            }
            if (sum < bestLocSum) {
                bestLocSum = sum;
                bestLoc = l;
            }
        }
        sol[f0] = bestLoc;
        assignedFacility[f0] = true;
        assignedLocation[bestLoc] = true;

        // Iteratively assign the remaining facilities.
        for (int k = 1; k < n; k++) {
            double bestCostIncrease = Double.MAX_VALUE;
            int bestF = -1;
            int bestL = -1;
            int[][] flow = problem.getFlowMatrix();
            // For each unassigned facility and location pair, compute the cost increase.
            for (int f = 0; f < n; f++) {
                if (!assignedFacility[f]) {
                    for (int l = 0; l < n; l++) {
                        if (!assignedLocation[l]) {
                            double costIncrease = 0;
                            // Sum cost increase over already assigned facilities.
                            for (int f2 = 0; f2 < n; f2++) {
                                if (assignedFacility[f2]) {
                                    int loc_f2 = sol[f2];
                                    costIncrease += flow[f][f2] * distance[l][loc_f2]
                                            + flow[f2][f] * distance[loc_f2][l];
                                    evaluationsCount++; // Count each evaluation for cost increase.
                                }
                            }
                            if (costIncrease < bestCostIncrease) {
                                bestCostIncrease = costIncrease;
                                bestF = f;
                                bestL = l;
                            }
                        }
                    }
                }
            }
            sol[bestF] = bestL;
            assignedFacility[bestF] = true;
            assignedLocation[bestL] = true;
            stepsCount++; // Each assignment is considered a step.
        }
        bestSolution.copyFrom(currentSolution);
    }
}
