package com.mycompany.qapsolver;

import java.util.List;

public interface NeighborhoodOperator {
    /**
     * Returns a list of all neighbor solutions from the given solution.
     * (Used for steepest descent.)
     */
    List<Solution> generateNeighbors(Solution current);

    /**
     * Returns one random neighbor solution from the given solution.
     * (Used for greedy, first-improvement search.)
     */
    Solution getRandomNeighbor(Solution current);
}
