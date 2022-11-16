package de.featjar.formula.analysis;

/**
 * A list of clauses.
 * Can be used to represent a conjunctive normal form (CNF) or any list of clauses in general.
 *
 * @param <T> the index type of the variables
 * @author Elias Kuiter
 */
public interface ClauseList<T> extends AssignmentList<Clause<T>> {
}
