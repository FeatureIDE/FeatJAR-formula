package de.featjar.formula.structure.term;

import de.featjar.formula.tmp.Formulas;

import java.util.List;

/**
 * A constant in a formula.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Constant extends NullaryTerm {

    protected Object value;

    private Constant(Constant constant) {
        super(constant);
        setValue(constant.getValue());
    }

    public Constant(Object value, Class<?> type) {
        super(null, type);
        setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        setName(String.valueOf(value));
    }

    @Override
    public Constant cloneNode() {
        return new Constant(this);
    }

    @Override
    public Object evaluate(List<?> values) {
        Formulas.assertInstanceOf(getType(), values);
        return value;
    }
}