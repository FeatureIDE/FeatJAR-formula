package de.featjar.formula.analysis;

import de.featjar.formula.structure.formula.Formula;

/**
 * Computes whether there is a solution for a given formula.
 *
 * @param <T> the type of the analysis input
 * @param <U> the index type of the variables
 * @author Elias Kuiter
 */
public interface HasSolutionAnalysis<T, U> extends Analysis.WithTimeout<T, Boolean>, Analysis.WithAssumptions<T, Boolean, U> {
    /**
     * Computes whether there is a solution for a formula given as an integer-indexed CNF.
     * Primarily used by satisfiability solvers, which usually expect this rigid format.
     */
    interface SAT extends HasSolutionAnalysis<ClauseList<Integer>, Integer> {
    }

    /**
     * Computes whether there is a solution for a given formula.
     * Primarily used by SMT solvers, which do not necessarily expect CNF or integer indices.
     */
    interface SMT extends HasSolutionAnalysis<Formula, String> {
    }
}
