package de.featjar.formula.analysis;

import de.featjar.base.computation.Analysis;
import de.featjar.base.computation.Computable;

/**
 * Computes a solution for a given formula.
 * Allows setting an optional timeout.
 * Allows passing an assignment with additional assumptions to make when solving the formula.
 *
 * @param <T> the type of the analysis input
 * @param <U> the type of the analysis result
 * @param <R> the type of the assignment
 * @author Elias Kuiter
 */
public interface GetSolutionAnalysis<T, U extends Solution<?>, R extends Assignment<?>> extends
        Analysis<T, U>,
        Computable.WithTimeout,
        FormulaAnalysis.WithAssumedAssignment<R> {
}
