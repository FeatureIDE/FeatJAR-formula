package de.featjar.formula.structure.term;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.NamedTermMap;
import de.featjar.formula.structure.TerminalFormula;
import de.featjar.formula.structure.TermMap;

import java.util.Objects;
import java.util.Optional;

/**
 * A nullary term in a formula.
 * Nullary terms are either variables or constants.
 * A nullary term is mapped to an index using a {@link TermMap}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class NullaryTerm extends TerminalFormula implements Term {

    protected String name;
    protected int index;
    protected Class<?> type;
    protected TermMap termMap;

    protected NullaryTerm(String name, int index, Class<?> type, TermMap termMap) {
        this.name = name;
        this.index = index;
        this.type = type;
        this.termMap = termMap;
    }

    protected abstract NullaryTerm copy(TermMap newMap);

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public void rename(String newName) {
        termMap.renameVariable(index, newName);
    }

    public int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public Class<?> getType() {
        return type;
    }

    void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public Optional<TermMap> getTermMap() {
        return Optional.of(termMap);
    }

    public void setTermMap(TermMap termMap) {
        this.termMap = termMap;
    }

    @Override
    public Term cloneNode() {
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equalsNode(Formula other) {
        if (this == other) return true;
        if (getClass() != other.getClass()) return false;
        return Objects.equals(name, ((NullaryTerm) other).name);
    }

    @Override
    public boolean equalsTree(Formula obj) {
        return equalsNode(obj);
    }

    @Override
    public String toString() {
        return index + ": " + name;
    }
}