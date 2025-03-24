package com.mycompany.qapsolver;

public class LocalSearchAlgorithm extends Algorithm {
    private final int maxIterations;

    public LocalSearchAlgorithm(Problem problem, int maxIterations) {
        super(problem);
        this.maxIterations = maxIterations;
    }

    @Override
    public void run() {
        // Initialize currentSolution using a random permutation.
        RandomSearchAlgorithm initializer = new RandomSearchAlgorithm(problem, 1);
        initializer.run();
        currentSolution.copyFrom(initializer.getBestSolution());
        bestSolution.copyFrom(currentSolution);

        int currentFitness = evaluate(currentSolution);
        int bestFitness = currentFitness;
        boolean improvementFound;

        // Simple first-improvement local search (swap neighborhood)
        for (int iter = 0; iter < maxIterations; iter++) {
            improvementFound = false;
            for (int i = 0; i < problem.getSize() - 1; i++) {
                for (int j = i + 1; j < problem.getSize(); j++) {
                    // Swap positions i and j in currentSolution.
                    currentSolution.swap(i, j);
                    int newFitness = evaluate(currentSolution);

                    if (newFitness < currentFitness) {
                        currentFitness = newFitness;
                        if (newFitness < bestFitness) {
                            bestFitness = newFitness;
                            bestSolution.copyFrom(currentSolution);
                        }
                        improvementFound = true;
                        // Accept the first improvement and break out of inner loops.
                        break;
                    } else {
                        // Revert the swap if no improvement.
                        currentSolution.swap(i, j);
                    }
                }
                if (improvementFound) {
                    break;
                }
            }
            // Terminate if no improving move is found.
            if (!improvementFound) {
                break;
            }
        }
    }
}
