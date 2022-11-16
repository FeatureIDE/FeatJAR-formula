package de.featjar.formula.analysis.value;

import de.featjar.formula.analysis.Assignment;

import java.util.HashMap;
import java.util.Map;

/**
 * Assigns values of any type to string-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of equalities for use in an SMT {@link de.featjar.formula.analysis.Solver}.
 *
 * @author Elias Kuiter
 */
public class ValueAssignment implements Assignment<String> {
    final Map<String, Object> variableValuePairs;

    public ValueAssignment() {
        this(new HashMap<>());
    }

    public ValueAssignment(Map<String, Object> variableValuePairs) {
        this.variableValuePairs = variableValuePairs;
    }

    public ValueAssignment(ValueAssignment valueAssignment) {
        this(new HashMap<>(valueAssignment.variableValuePairs));
    }

    public ValueAssignment toAssignment() {
        return new ValueAssignment(variableValuePairs);
    }

    public ValueClause toClause() {
        return new ValueClause(variableValuePairs);
    }

    public ValueSolution toSolution() {
        return new ValueSolution(variableValuePairs);
    }

    @Override
    public Map<String, Object> getAll() {
        return variableValuePairs;
    }

    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    protected ValueAssignment clone() {
        return toAssignment();
    }
}
