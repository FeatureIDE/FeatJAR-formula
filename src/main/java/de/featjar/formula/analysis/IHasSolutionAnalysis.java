package de.featjar.formula.analysis;

import de.featjar.base.computation.IAnalysis;
import de.featjar.base.computation.ITimeoutDependency;

/**
 * Computes whether there is a solution for a given formula.
 * Allows setting an optional timeout.
 * Allows passing an assignment with additional assumptions to make when solving the formula.
 *
 * @param <T> the type of the analysis input
 * @param <U> the type of the assignment
 * @author Elias Kuiter
 */
public interface IHasSolutionAnalysis<T, U extends IAssignment<?>> extends
        IAnalysis<T, Boolean>,
        ITimeoutDependency,
        IAssumedAssignmentDependency<U> {
}
