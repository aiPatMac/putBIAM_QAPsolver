package com.mycompany.qapsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class ThreeOptOperator implements NeighborhoodOperator {

    @Override
    public List<Solution> generateNeighbors(Solution current) {
        int n = current.getAssignment().length;
        List<Solution> neighbors = new ArrayList<>();
        // Iterate over all triples i < j < k.
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    // Extract the three elements.
                    int a = current.getAssignment()[i];
                    int b = current.getAssignment()[j];
                    int c = current.getAssignment()[k];
                    neighbors.add(applyThreeOpt(current, i, j, k, new int[]{a, c, b}));
                    neighbors.add(applyThreeOpt(current, i, j, k, new int[]{b, a, c}));
                    neighbors.add(applyThreeOpt(current, i, j, k, new int[]{c, b, a}));
                    neighbors.add(applyThreeOpt(current, i, j, k, new int[]{c, a, b}));
                    neighbors.add(applyThreeOpt(current, i, j, k, new int[]{b, c, a}));
                }
            }
        }
        return neighbors;
    }

    @Override
    public Solution getRandomNeighbor(Solution current) {
        int n = current.getAssignment().length;
        // Choose three distinct indices.
        int i = (int)(Math.random() * n);
        int j, k;
        do { j = (int)(Math.random() * n); } while (j == i);
        do { k = (int)(Math.random() * n); } while (k == i || k == j);
        int[] idx = new int[]{i, j, k};
        Arrays.sort(idx);
        // Choose one random permutation among the 5 options.
        int option = (int)(Math.random() * 5);
        int[] newTriple = null;
        switch(option) {
            case 0: newTriple = new int[]{current.getAssignment()[idx[0]], current.getAssignment()[idx[2]], current.getAssignment()[idx[1]]}; break;
            case 1: newTriple = new int[]{current.getAssignment()[idx[1]], current.getAssignment()[idx[0]], current.getAssignment()[idx[2]]}; break;
            case 2: newTriple = new int[]{current.getAssignment()[idx[2]], current.getAssignment()[idx[1]], current.getAssignment()[idx[0]]}; break;
            case 3: newTriple = new int[]{current.getAssignment()[idx[2]], current.getAssignment()[idx[0]], current.getAssignment()[idx[1]]}; break;
            case 4: newTriple = new int[]{current.getAssignment()[idx[1]], current.getAssignment()[idx[2]], current.getAssignment()[idx[0]]}; break;
        }
        return applyThreeOpt(current, idx[0], idx[1], idx[2], newTriple);
    }

    /**
     * Helper method that creates a new Solution by copying the current one
     * and then replacing the elements at positions i, j, k with newTriple.
     */
    private Solution applyThreeOpt(Solution current, int i, int j, int k, int[] newTriple) {
        int n = current.getAssignment().length;
        Solution neighbor = new Solution(n);
        neighbor.copyFrom(current);
        int[] arr = neighbor.getAssignment();
        arr[i] = newTriple[0];
        arr[j] = newTriple[1];
        arr[k] = newTriple[2];
        return neighbor;
    }
}
