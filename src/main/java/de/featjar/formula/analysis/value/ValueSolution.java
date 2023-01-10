package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.ISolution;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanSolution;
import java.util.LinkedHashMap;

/**
 * A (partial) value solution; that is, a conjunction of equalities.
 * Often holds output of an SMT {@link ISolver}.
 *
 * @author Elias Kuiter
 */
public class ValueSolution extends ValueAssignment implements ISolution<String> {
    public ValueSolution() {}

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
    public IComputation<BooleanSolution> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanSolution>) super.toBoolean(variableMap);
    }

    @Override
    public String toString() {
        return String.format("ValueSolution[%s]", print());
    }
}
