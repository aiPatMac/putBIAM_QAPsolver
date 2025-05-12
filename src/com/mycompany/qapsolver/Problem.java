package com.mycompany.qapsolver;

import java.io.*;
import java.util.*;

public class Problem {
    // Number of facilities/locations (QAP size)
    private final int size;

    // Flow matrix: flow[i][j] indicates the flow from facility i to facility j
    private final int[][] flowMatrix;

    // Distance matrix: distance[i][j] indicates the distance from location i to location j
    private final int[][] distanceMatrix;

    /**
     * Constructor that loads a QAP instance from a file.
     * The file format is expected to follow:
     * Line 1: single integer indicating the size n
     * Next n lines: n x n flow matrix (each line has n integers)
     * Next n lines: n x n distance matrix (each line has n integers)
     *
     * @param filename path to the QAP data file
     * @throws IOException if file is malformed or missing required data
     */
    public Problem(String filename) throws IOException {
        List<String> lines = new ArrayList<>();

        // Read all non-empty lines from the file
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }

        // Validate content
        if (lines.isEmpty()) {
            throw new IOException("File is empty: " + filename);
        }

        // First line: problem size
        this.size = Integer.parseInt(lines.get(0));

        // Initialize matrices
        flowMatrix = new int[size][size];
        distanceMatrix = new int[size][size];

        // Validate file contains enough data for both matrices
        if (lines.size() < 1 + 2 * size) {
            throw new IOException("Not enough data for a complete instance (size=" + size + ")");
        }

        int index = 1;

        // Load flow matrix (next `size` lines)
        for (int i = 0; i < size; i++) {
            String[] tokens = lines.get(index++).split("\\s+");
            if (tokens.length != size) {
                throw new IOException("Invalid number of elements in flow matrix row " + i);
            }
            for (int j = 0; j < size; j++) {
                flowMatrix[i][j] = Integer.parseInt(tokens[j]);
            }
        }

        // Load distance matrix (next `size` lines)
        for (int i = 0; i < size; i++) {
            String[] tokens = lines.get(index++).split("\\s+");
            if (tokens.length != size) {
                throw new IOException("Invalid number of elements in distance matrix row " + i);
            }
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = Integer.parseInt(tokens[j]);
            }
        }
    }

    /**
     * Alternative constructor useful for testing.
     * Allows you to directly provide flow and distance matrices in memory.
     */
    public Problem(int[][] flowMatrix, int[][] distanceMatrix) {
        this.size = flowMatrix.length;
        this.flowMatrix = flowMatrix;
        this.distanceMatrix = distanceMatrix;
    }

    // Accessor for problem size (number of facilities/locations)
    public int getSize() {
        return size;
    }

    // Accessor for the flow matrix
    public int[][] getFlowMatrix() {
        return flowMatrix;
    }

    // Accessor for the distance matrix
    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    /**
     * Utility method for printing the instance (size, flow and distance matrices).
     * Useful for debugging.
     */
    public void printInstance() {
        System.out.println("Size: " + size);
        System.out.println("Flow Matrix:");
        for (int[] row : flowMatrix) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("Distance Matrix:");
        for (int[] row : distanceMatrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
