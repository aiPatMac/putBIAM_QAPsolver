package com.mycompany.qapsolver;

import java.util.ArrayList;
import java.util.List;

public class TwoSwapOperator implements NeighborhoodOperator {

    @Override
    public List<Solution> generateNeighbors(Solution current) {
        int n = current.getAssignment().length;
        List<Solution> neighbors = new ArrayList<>();
        // Enumerate all pairs i < j.
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                Solution neighbor = new Solution(n);
                neighbor.copyFrom(current);
                neighbor.swap(i, j);
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    @Override
    public Solution getRandomNeighbor(Solution current) {
        int n = current.getAssignment().length;
        int i = (int)(Math.random() * n);
        int j;
        do {
            j = (int)(Math.random() * n);
        } while (j == i);
        Solution neighbor = new Solution(n);
        neighbor.copyFrom(current);
        neighbor.swap(i, j);
        return neighbor;
    }
}
