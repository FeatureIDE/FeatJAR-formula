package de.featjar.formula.analysis;

import de.featjar.base.computation.IAnalysis;
import de.featjar.base.computation.ITimeoutDependency;

/**
 * Computes all solutions for a given formula.
 * Allows setting an optional timeout.
 * Allows passing an assignment with additional assumptions to make when solving the formula.
 * May return a subset of solutions with a warning if the timeout is reached.
 *
 * @param <T> the type of the analysis input
 * @param <U> the type of the analysis result
 * @param <R> the type of the assignment
 * @author Elias Kuiter
 */
public interface IGetSolutionsAnalysis<T, U extends IAssignmentList<? extends ISolution<?>>, R extends IAssignment<?>> extends
        IAnalysis<T, U>,
        ITimeoutDependency,
        IAssumedAssignmentDependency<R> {
}
