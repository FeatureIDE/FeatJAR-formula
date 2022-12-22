package de.featjar.formula.analysis;

import de.featjar.base.computation.IAnalysis;
import de.featjar.base.computation.IComputation;

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
public interface IGetSolutionAnalysis<T, U extends ISolution<?>, R extends IAssignment<?>> extends
        IAnalysis<T, U>,
        IComputation.WithTimeout,
        IFormulaAnalysis.WithAssumedAssignment<R> {
}
