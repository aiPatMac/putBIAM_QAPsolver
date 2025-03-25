package com.mycompany.qapsolver;

import java.util.Collections;
import java.util.List;

public abstract class OperatorLocalSearchAlgorithm extends LocalSearchAlgorithm {
    protected final NeighborhoodOperator operator;

    public OperatorLocalSearchAlgorithm(Problem problem, int maxIterations, int randomStarts, NeighborhoodOperator operator) {
        super(problem, maxIterations, randomStarts);
        this.operator = operator;
    }
}
