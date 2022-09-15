package de.featjar.formula.structure.term.value;

import de.featjar.formula.structure.NonTerminalExpression;
import de.featjar.formula.structure.TerminalExpression;
import de.featjar.formula.tmp.Formulas;

import java.util.List;

/**
 * A constant.
 * Is identified by its value.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Constant extends TerminalExpression implements Value {
    protected Object value;
    protected Class<?> type;

    private Constant(Constant constant) {
        setValue(constant.value);
        setType(constant.type);
    }

    public Constant(Object value, Class<?> type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return String.valueOf(value);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public Constant cloneNode() {
        return new Constant(this);
    }

    @Override
    public Object evaluate(List<?> values) {
        if (!getType().isInstance(values))
            throw new IllegalArgumentException("value not of type " + getType());
        return value;
    }

    //todo override equalsNode with value
}