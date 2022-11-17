package de.featjar.formula.analysis;

import java.math.BigInteger;

/**
 * Counts the number of solutions for a given formula.
 * Allows setting an optional timeout.
 * Allows passing an assignment with additional assumptions to make when solving the formula.
 * May return a lower bound with a warning if the timeout is reached.
 *
 * @param <T> the type of the analysis input
 * @param <U> the type of the assignment
 * @author Elias Kuiter
 */
public interface CountSolutionsAnalysis<T, U extends Assignment<?>> extends
        Analysis<T, BigInteger>,
        Analysis.WithTimeout,
        Analysis.WithAssumedAssignment<U> {
}
