package com.mycompany.qapsolver;

public class TimeBudgetRange {
    public final long minTime;
    public final long maxTime;

    public TimeBudgetRange(long minTime, long maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public long randomBudget() {
        // Using Math.random() for simplicity.
        return minTime + (long)(Math.random() * (maxTime - minTime));
    }
}
