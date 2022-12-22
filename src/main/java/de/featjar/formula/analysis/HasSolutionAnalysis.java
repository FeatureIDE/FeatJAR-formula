package de.featjar.formula.analysis;

import de.featjar.base.computation.IAnalysis;
import de.featjar.base.computation.IComputation;

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
        IAnalysis<T, Boolean>,
        IComputation.WithTimeout,
        IFormulaAnalysis.WithAssumedAssignment<U> {
}
