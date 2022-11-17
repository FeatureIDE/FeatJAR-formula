package de.featjar.formula.analysis.bool;

import de.featjar.base.data.Pair;
import de.featjar.base.data.RangeMap;
import de.featjar.formula.structure.Expression;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Maps variable names to indices and vice versa.
 * Used to link a literal index in a {@link BooleanAssignment} to a {@link de.featjar.formula.structure.term.value.Variable}
 * in a {@link de.featjar.formula.structure.formula.Formula}.
 *
 * @author Elias Kuiter
 */
public class VariableMap extends RangeMap<String> {
    public VariableMap() {
    }

    protected VariableMap(Expression expression) {
        super(expression.getVariableNames());
    }

    public VariableMap(VariableMap variableMap) {
        super(variableMap.getObjects());
    }

    /**
     * Creates a variable map from an expression.
     * Indices are numbered by the occurrence of variables in a preorder traversal.
     *
     * @param expression the expression
     */
    public static VariableMap of(Expression expression) {
       return new VariableMap(expression);
    }

    public static VariableMap empty() {
        return new VariableMap();
    }

    public List<String> getVariableNames() {
        return getObjects().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Integer> getVariableIndices() {
        return stream().map(Pair::getKey).collect(Collectors.toList());
    }

    public int getVariableCount() {
        return getVariableNames().size();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public VariableMap clone() {
        return new VariableMap(this);
    }
}
