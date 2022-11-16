package de.featjar.formula.analysis.solver;

public interface Clause<T> extends Assignment<T> {
    Solution<T> toSolution();
}
