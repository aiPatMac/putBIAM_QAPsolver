package com.mycompany.qapsolver;

import java.util.Arrays;
import java.util.Random;

public class Solution {
    private final int[] assignment;

    public Solution(int size) {
        this.assignment = new int[size];
        // Initialize with the identity permutation.
        for (int i = 0; i < size; i++) {
            assignment[i] = i;
        }
        // Shuffle the array to obtain a random permutation.
        Random rand = new Random();
        for (int i = size - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = assignment[i];
            assignment[i] = assignment[j];
            assignment[j] = temp;
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
