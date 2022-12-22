package de.featjar.formula.analysis;

import de.featjar.base.computation.Analysis;
import de.featjar.base.computation.Computable;

/**
 * Computes whether there is a solution for a given formula.
 * Allows setting an optional timeout.
 * Allows passing an assignment with additional assumptions to make when solving the formula.
 *
 * @param <T> the type of the analysis input
 * @param <U> the type of the assignment
 * @author Elias Kuiter
 */
public interface HasSolutionAnalysis<T, U extends Assignment<?>> extends
        Analysis<T, Boolean>,
        Computable.WithTimeout,
        FormulaAnalysis.WithAssumedAssignment<U> {
}
