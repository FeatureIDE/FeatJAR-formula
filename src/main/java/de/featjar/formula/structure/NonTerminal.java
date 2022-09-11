/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.structure;

import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.structure.Tree;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public abstract class NonTerminal extends Tree<Formula> implements Formula {
    private int hashCode = 0;
    private boolean hasHashCode = false;

    protected NonTerminal() {
        super();
    }

    protected NonTerminal(List<? extends Formula> children) {
        super();
        ensureSharedVariableMap(children);
        super.setChildren(children);
    }

    protected NonTerminal(Formula... children) {
        this(Arrays.asList(children));
    }

    protected void ensureSharedVariableMap(List<? extends Formula> children) {
        VariableMap firstElement = null;
        for (Formula element : children) {
            if (firstElement == null) {
                firstElement = element.getVariableMap().orElse(null);
            } else {
                if (firstElement != element.getVariableMap().orElse(firstElement)) {
                    throw new IllegalArgumentException(
                            "tried to instantiate formula with different variable maps. perhaps you meant to use Formulas.compose(...)?");
                }
            }
        }
    }

    protected void ensureSharedVariableMap(Formula newChild) {
        if (getVariableMap().orElse(null) != newChild.getVariableMap().orElse(null))
            throw new IllegalArgumentException(
                    "tried to add formula with different variable map. perhaps you meant to use Formulas.compose(...)?");
    }

    //	@Override
    //	public void setVariableMap(VariableMap map) {
    ////		Formulas.manipulate(this, new VariableMapSetter(map));
    ////		for (ListIterator<Formula> it = children.listIterator(); it.hasNext();) {
    ////			final Formula child = it.next();
    ////			if (child instanceof Variable) {
    ////				final Variable replacement = map.getVariable(child.getName()).orElseThrow(
    ////					() -> new IllegalArgumentException(
    ////						"Map does not contain variable with name " + child.getName()));
    ////				if (replacement != child) {
    ////					it.set(replacement);
    ////				}
    ////			} else if (child instanceof Constant) {
    ////				final Constant replacement = map.getConstant(child.getName()).orElseThrow(
    ////					() -> new IllegalArgumentException(
    ////						"Map does not contain constant with name " + child.getName()));
    ////				if (replacement != child) {
    ////					it.set(replacement);
    ////				}
    ////			} else {
    ////				child.setVariableMap(map);
    ////			}
    ////		}
    //	}

    @Override
    public void setChildren(List<? extends Formula> children) {
        ensureSharedVariableMap(children); // TODO
        super.setChildren(children);
        hasHashCode = false;
    }

    @Override
    public void addChild(int index, Formula newChild) {
        ensureSharedVariableMap(newChild);
        super.addChild(index, newChild);
        hasHashCode = false;
    }

    @Override
    public void addChild(Formula newChild) {
        ensureSharedVariableMap(newChild);
        super.addChild(newChild);
        hasHashCode = false;
    }

    @Override
    public void removeChild(Formula child) {
        super.removeChild(child);
        hasHashCode = false;
    }

    @Override
    public Formula removeChild(int index) {
        Formula expression = super.removeChild(index);
        hasHashCode = false;
        return expression;
    }

    @Override
    public void replaceChild(Formula oldChild, Formula newChild) {
        ensureSharedVariableMap(newChild);
        super.replaceChild(oldChild, newChild);
        hasHashCode = false;
    }

    @Override
    public int hashCode() {
        if (!hasHashCode) {
            int tempHashCode = computeHashCode();
            for (final Formula child : children) {
                tempHashCode += (tempHashCode * 37) + child.hashCode();
            }
            hashCode = tempHashCode;
            hasHashCode = true;
        }
        return hashCode;
    }

    protected int computeHashCode() {
        return Objects.hash(getClass(), children.size());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NonTerminal) && equalsTree((NonTerminal) obj);
    }

    @Override
    public boolean equalsTree(Formula other) {
        return (other instanceof NonTerminal) && Trees.equals(this, (NonTerminal) other);
    }

    @Override
    public boolean equalsNode(Formula other) {
        return (getClass() == other.getClass()) && (children.size() == ((NonTerminal) other).children.size());
    }

    @Override
    public String toString() {
        if (hasChildren()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append("[");
            for (final Formula child : children) {
                sb.append(child.getName());
                sb.append(", ");
            }
            sb.replace(sb.length() - 2, sb.length(), "]");
            return sb.toString();
        } else {
            return getName();
        }
    }
}
