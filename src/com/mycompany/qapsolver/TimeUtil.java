package com.mycompany.qapsolver;

public class TimeUtil {
    // Returns the current time in nanoseconds.
    public static long currentTime() {
        return System.nanoTime();
    }

    // Returns elapsed time (in milliseconds) from the provided start time.
    public static double elapsedMillis(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000.0;
    }

    // Returns elapsed time in microseconds.
    public static double elapsedMicros(long startTime) {
        return (System.nanoTime() - startTime) / 1_000.0;
    }

    // Returns elapsed time in seconds.
    public static double elapsedSeconds(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000_000.0;
    }
}
