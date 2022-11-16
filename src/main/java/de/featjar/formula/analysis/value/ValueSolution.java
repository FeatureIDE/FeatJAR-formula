package de.featjar.formula.analysis.value;

import de.featjar.formula.analysis.Solution;
import de.featjar.formula.analysis.Solver;

import java.util.HashMap;
import java.util.Map;

/**
 * A (partial) value solution; that is, a conjunction of equalities.
 * Often holds output of an SMT {@link Solver}.
 *
 * @author Elias Kuiter
 */
public class ValueSolution extends ValueAssignment implements Solution<String> {
    public ValueSolution() {
    }

    public ValueSolution(Map<String, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueSolution(ValueClause predicateClause) {
        this(new HashMap<>(predicateClause.variableValuePairs));
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    protected ValueSolution clone() {
        return toSolution();
    }
}
