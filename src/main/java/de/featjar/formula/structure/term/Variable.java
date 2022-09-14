package de.featjar.formula.structure.term;

import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.TermMap;

import java.util.List;

/**
 * A variable in a formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Variable extends NullaryTerm {
    // todo inv: all subtrees have the same variablemap

    public Variable(String name, int index, Class<?> type, TermMap termMap) {
        super(name, index, type, termMap);
    }

    @Override
    protected Variable copy(TermMap newMap) {
        return new Variable(name, index, type, newMap);
    }

    @Override
    public Object evaluate(List<?> values) {
        Formulas.assertInstanceOf(getType(), values);
        return values.get(0);
    }
}