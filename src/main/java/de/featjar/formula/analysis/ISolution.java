package de.featjar.formula.analysis;

import de.featjar.formula.analysis.bool.BooleanSolution;
import de.featjar.formula.analysis.value.ValueSolution;

/**
 * A solution; that is, a conjunction of generalized literals.
 * Implemented as an assignment of variables to values, which are, by convention, interpreted as a conjunction.
 * That is, a clause is fulfilled if all variables match their assigned values.
 * Thus, this class generalizes the notion of a solution (also known as satisfying assignment or model)
 * in propositional logic to also account for first-order logic.
 * For a propositional implementation, see {@link BooleanSolution},
 * for a first-order implementation, see {@link ValueSolution}.
 * If you need a disjunction of generalized literals, consider using a {@link IClause} instead.
 *
 * @param <T> the index type of the variables
 * @author Elias Kuiter
 */
public interface ISolution<T> extends IAssignment<T> {
    /**
     * {@return a clause equivalent to this solution}
     * You can use this method to re-interpret a (conjunctive) solution as a (disjunctive) clause.
     */
    IClause<T> toClause();
}
