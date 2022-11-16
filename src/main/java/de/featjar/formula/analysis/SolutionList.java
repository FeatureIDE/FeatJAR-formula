package de.featjar.formula.analysis;

/**
 * A list of solutions.
 * Can be used to represent a disjunctive normal form (DNF) or any list of solutions in general.
 *
 * @param <T> the index type of the variables
 * @author Elias Kuiter
 */
public interface SolutionList<T> extends AssignmentList<Solution<T>> {
}
