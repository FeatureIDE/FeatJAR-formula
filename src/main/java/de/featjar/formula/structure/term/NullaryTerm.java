package de.featjar.formula.structure.term;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.TerminalFormula;

import java.util.Objects;

/**
 * A nullary term in a formula.
 * Nullary terms are either variables or constants.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class NullaryTerm extends TerminalFormula implements Term {

    protected String name;
    protected Class<?> type;

    protected NullaryTerm(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    protected NullaryTerm(NullaryTerm nullaryTerm) {
        setName(nullaryTerm.name);
        setType(nullaryTerm.type);
    }

    @Override
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public boolean equalsNode(Formula other) {
        return super.equalsNode(other) && Objects.equals(name, ((NullaryTerm) other).name) &&
                Objects.equals(type, ((NullaryTerm) other).type);
    }
}