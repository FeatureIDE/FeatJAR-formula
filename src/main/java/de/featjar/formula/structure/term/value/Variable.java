package de.featjar.formula.structure.term.value;

import de.featjar.formula.structure.NonTerminalExpression;
import de.featjar.formula.structure.TerminalExpression;
import de.featjar.formula.tmp.Formulas;

import java.util.List;

/**
 * A variable.
 * Is identified by its name.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Variable extends TerminalExpression implements Value {
    protected String name;
    protected Class<?> type;

    private Variable(Variable variable) {
        setName(variable.name);
        setType(variable.type);
    }

    public Variable(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public Variable(String name) {
        this(name, Boolean.class);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public Variable cloneNode() {
        return new Variable(this);
    }

    @Override
    public Object evaluate(List<?> values) {
        if (!getType().isInstance(values))
            throw new IllegalArgumentException("value not of type " + getType());
        return values.get(0);
    }
}