package de.featjar.formula.analysis.value;

import de.featjar.base.data.Result;
import de.featjar.formula.analysis.Clause;
import de.featjar.formula.analysis.Solver;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.analysis.bool.BooleanClause;
import de.featjar.formula.analysis.bool.VariableMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public ValueClause(LinkedHashMap<String, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueClause(ValueClause predicateClause) {
        this(new LinkedHashMap<>(predicateClause.variableValuePairs));
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    protected ValueClause clone() {
        return toClause();
    }

    @Override
    public Result<BooleanClause> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @Override
    public String toString() {
        return String.format("ValueClause[%s]", print());
    }
}
