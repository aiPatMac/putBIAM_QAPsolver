package com.mycompany.qapsolver;

public class Config {
    // Experiment configuration
    public static final int RUNS_PER_INSTANCE = 10;    // number of runs per instance
    public static final int MAX_INSTANCES = 10;        // maximum number of working instances to process

    // Random Search (RS) parameters (iterations are used as fallback if time-limit not applied)
    public static final int RS_ITERATIONS = 100000;
    // Random Walk (RW) parameters
    public static final int RW_ITERATIONS = 100000;

    // Greedy and Steepest Descent (G and S) parameters used for dynamic time-budget estimation.
    public static final int GS_MAX_ITERATIONS = 10000;
    public static final int GS_RANDOM_STARTS = 10;
}
