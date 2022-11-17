package de.featjar.formula.analysis.solver;

public interface Solution<T> extends Assignment<T> {
    Clause<T> toClause();
}
