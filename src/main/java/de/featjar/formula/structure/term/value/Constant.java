package de.featjar.formula.structure.term.value;

import de.featjar.formula.structure.ATerminalExpression;
import de.featjar.formula.structure.IExpression;
import java.util.List;
import java.util.Objects;

/**
 * A constant.
 * Is identified by its value.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 * @deprecated currently not supported by any meaningful operations
 */
@Deprecated
public class Constant extends ATerminalExpression implements IValue {
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
    public boolean equalsNode(IExpression other) {
        return super.equalsNode(other) && Objects.equals(value, ((Constant) other).value);
    }

    @Override
    public int hashCodeNode() {
        return Objects.hash(super.hashCodeNode(), value);
    }

    @Override
    public Object evaluate(List<?> values) {
        return value;
    }
}
