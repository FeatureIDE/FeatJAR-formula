/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.analysis.solver;

import de.featjar.base.data.Result;

import java.math.BigInteger;
import java.util.List;

/**
 * A solver that analyzes a given formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Solver<T, C extends Clause<T>, S extends Solution<T>> {
    /**
     * {@return the formula analyzed by this solver in the solver's internal format}
     */
    SolverFormula<?> getSolverFormula();

    /**
     * {@return list of additional assumptions that this solver should consider}
     */
    Assignment<T> getAssumptionList(); // ClauseList // simple assumptions

    /**
     * {@return list of additional assumptions that this solver should consider}
     */
    CNF<T> getAssumptionList(); // ClauseList // complex assumptions

    /**
     * Sets additional assumptions this solver should consider.
     *
     * @param assumptions the assumptions in form of a (partial) variable assignment
     */
    void setAssumptionList(AssumptionList<?> assumptionList); // throws SolverContradictionException; // todo: exception needed? maybe put this in the constructor of CNF/DNF?

    long getTimeout();

    void setTimeout(long timeoutInMs);

    /**
     * Resets any internal state of this solver.
     * Should be overridden to allow for reusing this solver instance. todo: is this a good idea with multithreading? or rather use solverSupplier in Analysis?
     */
    default void reset() {}

    /**
     * {@return whether there is a solution for the given formula}
     * This method characterizes satisfiability solvers.
     */
    default Result<Boolean> hasSolution() {
        return Result.empty(new UnsupportedOperationException());
    }

    default HasSolutionAnalysis gibmireinehassolutionanalyse() {
        return ...;
    }

    /**
     * {@return a solution for the given formula, if any}
     * This method characterizes solution solvers.
     */
    default Result<S> getSolution() {
        return Result.empty(new UnsupportedOperationException());
    }

    /**
     * {@return the number of solutions for the given formula}
     * This method characterizes #SAT (SharpSAT) solvers.
     */
    default Result<BigInteger> countSolutions() {
        return Result.empty(new UnsupportedOperationException());
    }

    /**
     * {@return all solutions for the given formula}
     * This method characterizes all-solution (AllSAT) solvers.
     */
    default Result<S> getSolutions() { // SolutionList
        return Result.empty(new UnsupportedOperationException());
    }

    /**
     * {@return a minimal unsatisfiable subset (MUS) for the given formula, if any}
     * This method characterizes solvers that extract a MUS from an unsatisfiable formula.
     * A minimal unsatisfiable subset is any unsatisfiable subset of a formula that cannot be reduced any
     * further without becoming satisfiable, thus explaining why the formula is unsatisfiable.
     * This extraction is only possible when the given formula is not satisfiable.
     */
    default Result<List<C>> getMinimalUnsatisfiableSubset() {
        return Result.empty(new UnsupportedOperationException());
    }

    /**
     * {@return all minimal unsatisfiable subsets (MUS) for the given formula}
     */
    default Result<List<List<C>>> getAllMinimalUnsatisfiableSubsets() {
        return Result.empty(new UnsupportedOperationException());
    }

    /**
     * {@return the smallest value for a variable to still satisfy the given formula}
     * This method characterizes SMT (satisfiability modulo theories) solvers.
     *
     * @param variable the variable to minimize
     */
//    default Result<U> minimize(T variable) {
//        return Result.empty(new UnsupportedOperationException());
//    }

    /**
     * {@return the largest value for a variable to still satisfy the given formula}
     * @param variable the variable to maximize
     */
//    default Result<U> maximize(T variable) {
//        return Result.empty(new UnsupportedOperationException());
//    }

    /**
     * {@return whether this solver supports {@link #hasSolution()}}
     */
    default boolean supportsHasSolution() {
        return false;
    }

    /**
     * {@return whether this solver supports {@link #getSolution()}
     */
    default boolean supportsGetSolution() {
        return false;
    }

    /**
     * {@return whether this solver supports {@link #countSolutions()}
     */
    default boolean supportsCountSolutions() {
        return false;
    }

    /**
     * {@return whether this solver supports {@link #getSolutions()}
     */
    default boolean supportsGetSolutions() {
        return false;
    }

    /**
     * {@return whether this solver supports {@link #getMinimalUnsatisfiableSubset()}}
     */
    default boolean supportsGetMinimalUnsatisfiableSubset() {
        return false;
    }

    /**
     * {@return whether this solver supports {@link #getMinimalUnsatisfiableSubsets()}}
     */
    default boolean supportsGetMinimalUnsatisfiableSubsets() {
        return false;
    }

    //todo: supports SMT
}
