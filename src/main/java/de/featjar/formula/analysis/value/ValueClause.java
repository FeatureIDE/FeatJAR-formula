package de.featjar.formula.analysis.value;

import de.featjar.base.computation.Computable;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.Clause;
import de.featjar.formula.analysis.Solver;
import de.featjar.formula.analysis.bool.BooleanClause;
import de.featjar.formula.analysis.VariableMap;

import java.util.LinkedHashMap;

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

    @SuppressWarnings("unchecked")
    @Override
    public Computable<BooleanClause> toBoolean(Computable<VariableMap> variableMap) {
        return (Computable<BooleanClause>) super.toBoolean(variableMap);
    }

    @Override
    public String toString() {
        return String.format("ValueClause[%s]", print());
    }
}
