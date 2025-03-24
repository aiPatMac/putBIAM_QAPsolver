package com.mycompany.qapsolver;

import java.io.*;
import java.util.*;

public class Problem {
    private final int size;
    private final int[][] flowMatrix;
    private final int[][] distanceMatrix;

    public Problem(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        if (lines.isEmpty()) {
            throw new IOException("File is empty: " + filename);
        }
        this.size = Integer.parseInt(lines.get(0));
        flowMatrix = new int[size][size];
        distanceMatrix = new int[size][size];
        if (lines.size() < 1 + 2 * size) {
            throw new IOException("Not enough data for a complete instance (size=" + size + ")");
        }
        int index = 1;
        // Load flow matrix
        for (int i = 0; i < size; i++) {
            String[] tokens = lines.get(index++).split("\\s+");
            if (tokens.length != size) {
                throw new IOException("Invalid number of elements in flow matrix row " + i);
            }
            for (int j = 0; j < size; j++) {
                flowMatrix[i][j] = Integer.parseInt(tokens[j]);
            }
        }
        // Load distance matrix
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

    // Alternative constructor for testing using direct matrices.
    public Problem(int[][] flowMatrix, int[][] distanceMatrix) {
        this.size = flowMatrix.length;
        this.flowMatrix = flowMatrix;
        this.distanceMatrix = distanceMatrix;
    }

    public int getSize() {
        return size;
    }

    public int[][] getFlowMatrix() {
        return flowMatrix;
    }

    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

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
