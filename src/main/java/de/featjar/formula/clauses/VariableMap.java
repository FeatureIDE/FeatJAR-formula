package de.featjar.formula.clauses;

import de.featjar.base.data.Pair;
import de.featjar.base.data.RangeMap;
import de.featjar.formula.structure.Expression;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Maps variable names to indices and vice versa.
 *
 * @author Elias Kuiter
 */
public class VariableMap extends RangeMap<String> {
    protected VariableMap() {
    }

    protected VariableMap(Expression expression) {
        super(expression.getVariableNames());
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
        return super.getObjects().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Integer> getVariableIndices() {
        return super.stream().map(Pair::getKey).collect(Collectors.toList());
    }

    public int getVariableCount() {
        return getVariableNames().size();
    }
}
