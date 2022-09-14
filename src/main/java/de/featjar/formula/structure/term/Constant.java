package de.featjar.formula.structure.term;

import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.NamedTermMap;
import de.featjar.formula.structure.TermMap;

import java.util.List;

/**
 * A constant in a formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Constant extends NullaryTerm {

    private final Object value;

    public Constant(String name, int index, Class<?> type, TermMap termMap, Object value) {
        super(name, index, type, termMap);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Constant copy(TermMap newMap) {
        return new Constant(name, index, type, newMap, value);
    }

    @Override
    public String toString() {
        return index + ": " + name + " (" + value + ")";
    }

    @Override
    public Object evaluate(List<?> values) {
        Formulas.assertInstanceOf(getType(), values);
        return value;
    }
}