package de.featjar.formula.analysis.value;

import de.featjar.base.computation.Computable;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.Solution;
import de.featjar.formula.analysis.Solver;
import de.featjar.formula.analysis.bool.BooleanSolution;
import de.featjar.formula.analysis.VariableMap;

import java.util.LinkedHashMap;

/**
 * A (partial) value solution; that is, a conjunction of equalities.
 * Often holds output of an SMT {@link Solver}.
 *
 * @author Elias Kuiter
 */
public class ValueSolution extends ValueAssignment implements Solution<String> {
    public ValueSolution() {
    }

    public ValueSolution(LinkedHashMap<String, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueSolution(ValueClause predicateClause) {
        this(new LinkedHashMap<>(predicateClause.variableValuePairs));
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    protected ValueSolution clone() {
        return toSolution();
    }

    @Override
    public Result<BooleanSolution> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Computable<BooleanSolution> toBoolean(Computable<VariableMap> variableMap) {
        return (Computable<BooleanSolution>) super.toBoolean(variableMap);
    }

    @Override
    public String toString() {
        return String.format("ValueSolution[%s]", print());
    }
}
