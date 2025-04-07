package com.mycompany.qapsolver;

public class Config {
    // Experiment configuration
    public static final int RUNS_PER_INSTANCE = 20;    // number of runs per instance
    public static final int MAX_INSTANCES = 13;          // maximum number of working instances to process

    // Random Search (RS) parameters (iterations are used as fallback if time-limit not applied)
    public static final int RS_ITERATIONS = 10;
    // Random Walk (RW) parameters
    public static final int RW_ITERATIONS = 10;

    // Greedy and Steepest Descent (G and S) parameters used for dynamic time-budget estimation.
    public static final int GS_MAX_ITERATIONS = 10000;
    public static final int GS_RANDOM_STARTS = 10;

    // Nearest Neighbor (heuristic) parameters:
    // If NN_RANDOM_START is true, a random facility is chosen;
    // otherwise, NN_FIXED_START_FACILITY is used.
    public static final boolean NN_RANDOM_START = true;
    public static final int NN_FIXED_START_FACILITY = 0;

    // Similarly for the starting location.
    public static final boolean NN_RANDOM_LOCATION = true;
    public static final int NN_FIXED_START_LOCATION = 0;
}
