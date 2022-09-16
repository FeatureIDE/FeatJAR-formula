package de.featjar.formula.structure.term.value;

import de.featjar.formula.structure.TerminalExpression;

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
        if (!type.isInstance(value))
            throw new IllegalArgumentException(
                    String.format("expected value of type %s, got %s", getType(), value.getClass()));
        this.value = value;
        this.type = type;
    }

    public Constant(Object value) {
        this.value = value;
        this.type = value.getClass();
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
        return value;
    }

    //todo override equalsNode with value
}