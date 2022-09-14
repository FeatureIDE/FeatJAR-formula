package de.featjar.formula.structure.term;

import de.featjar.formula.tmp.Formulas;

import java.util.List;

/**
 * A variable in a formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Variable extends NullaryTerm {
    private Variable(Variable variable) {
        super(variable);
    }

    public Variable(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Variable cloneNode() {
        return new Variable(this);
    }

    @Override
    public Object evaluate(List<?> values) {
        Formulas.assertInstanceOf(getType(), values);
        return values.get(0);
    }
}