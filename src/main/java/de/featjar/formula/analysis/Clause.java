package de.featjar.formula.analysis;

import de.featjar.formula.analysis.bool.BooleanClause;
import de.featjar.formula.analysis.value.ValueClause;

/**
 * A clause; that is, a disjunction of literals or equalities.
 * Implemented as an assignment of variables to values, which are, by convention, interpreted as a disjunction.
 * That is, a clause is fulfilled if at least one variable matches its assigned value.
 * Thus, this class generalizes the notion of a clause in propositional logic to also account for first-order logic.
 * For a propositional implementation, see {@link BooleanClause},
 * for a first-order implementation, see {@link ValueClause}.
 * If you need a conjunction of literals or equalities, consider using a {@link Solution} instead.
 *
 * @param <T> the index type of the variables
 * @author Elias Kuiter
 */
public interface Clause<T> extends Assignment<T> {
    /**
     * {@return a solution equivalent to this clause}
     * You can use this method to re-interpret a (disjunctive) clause as a (conjunctive) solution.
     */
    Solution<T> toSolution();
}
