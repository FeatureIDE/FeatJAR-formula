package de.featjar.formula.analysis.value;

import de.featjar.formula.analysis.Clause;
import de.featjar.formula.analysis.Solver;

import java.util.HashMap;
import java.util.Map;

/**
 * A value clause; that is, a disjunction of equalities.
 * Often used as input to an SMT {@link Solver}.
 *
 * @author Elias Kuiter
 */
public class ValueClause extends ValueAssignment implements Clause<String> {
    public ValueClause() {
    }

    public ValueClause(Map<String, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueClause(ValueClause predicateClause) {
        this(new HashMap<>(predicateClause.variableValuePairs));
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    protected ValueClause clone() {
        return toClause();
    }
}
