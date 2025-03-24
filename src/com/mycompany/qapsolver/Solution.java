package com.mycompany.qapsolver;

import java.util.Arrays;

public class Solution {
    private final int[] assignment;

    public Solution(int size) {
        this.assignment = new int[size];
        for (int i = 0; i < size; i++) {
            assignment[i] = i;
        }
    }

    public int[] getAssignment() {
        return assignment;
    }

    // Swap two elements in-place.
    public void swap(int i, int j) {
        int temp = assignment[i];
        assignment[i] = assignment[j];
        assignment[j] = temp;
    }

    // Copy the contents from another solution (assumes same size).
    public void copyFrom(Solution other) {
        System.arraycopy(other.assignment, 0, this.assignment, 0, assignment.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(assignment);
    }
}
