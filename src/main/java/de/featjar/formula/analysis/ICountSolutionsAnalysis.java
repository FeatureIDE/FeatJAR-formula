package de.featjar.formula.analysis;

import de.featjar.base.computation.IAnalysis;
import de.featjar.base.computation.IComputation;

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
public interface ICountSolutionsAnalysis<T, U extends IAssignment<?>> extends
        IAnalysis<T, BigInteger>,
        IComputation.WithTimeout,
        IFormulaAnalysis.WithAssumedAssignment<U> {
}
