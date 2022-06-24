/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.structure;

import java.util.*;
import java.util.stream.Collectors;

import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.formula.structure.compound.And;
import org.spldev.util.tree.*;
import org.spldev.util.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public abstract class NonTerminal extends AbstractNonTerminal<Expression> implements Expression {
	/**
	 * If Compose.FALSE is supplied (the default), child formulas are not cloned.
	 * This requires that all involved formulas share one variable map. This is
	 * useful e.g. for assembling the formula of a single feature model. If
	 * Compose.TRUE is supplied, child formulas are cloned and their variable maps
	 * merged. That is, the composed formula exists independently of its children.
	 * This is useful e.g. for composing several feature model (interface) formulas.
	 */
	public enum Compose {
		TRUE, FALSE
	}

	private int hashCode = 0;
	private boolean hasHashCode = false;

	protected NonTerminal() {
		super();
	}

	protected NonTerminal(Compose compose, List<? extends Expression> children) {
		super();
		ensureSharedVariableMap(compose, children);
		super.setChildren(compose == Compose.TRUE ? compose(children) : children);
	}

	protected NonTerminal(List<? extends Expression> children) {
		this(Compose.FALSE, children);
	}

	@SafeVarargs
	protected NonTerminal(Compose compose, Expression... children) {
		this(compose, Arrays.asList(children));
	}

	@SafeVarargs
	protected NonTerminal(Expression... children) {
		this(Compose.FALSE, children);
	}

	protected void ensureSharedVariableMap(Compose compose, List<? extends Expression> children) {
		if (compose == Compose.FALSE) {
			children.stream().map(Expression::getVariableMap).reduce((acc, val) -> {
				if (acc != val)
					throw new IllegalArgumentException(
						"tried to instantiate formula with different variable maps. perhaps you meant to use Compose.TRUE?");
				return val;
			});
		}
	}

	protected void ensureSharedVariableMap(Compose compose, Expression newChild) {
		if (compose == Compose.FALSE) {
			if (getVariableMap() != newChild.getVariableMap())
				throw new IllegalArgumentException(
					"tried to add formula with different variable map. perhaps you meant to use Compose.TRUE?");
		}
	}

	/**
	 * Composes formulas (e.g., for feature model fragments and interfaces) by
	 * cloning and variable map merging. Assumes that the supplied formulas are
	 * partly independent, partly dependent (on common variables). Leaves the input
	 * formulas and their variable maps untouched by returning copies.
	 */
	public static List<? extends Expression> compose(List<? extends Expression> children) {
		VariableMap composedMap = VariableMap.merge(
			children.stream().map(Expression::getVariableMap).collect(Collectors.toList()));
		return children.stream()
			.map(Trees::cloneTree)
			.peek(formula -> {
				formula.setVariableMap(composedMap);
				formula.adaptVariableMap(composedMap);
			})
			.collect(Collectors.toList());
	}

	@Override
	public void setChildren(List<? extends Expression> children) {
		ensureSharedVariableMap(Compose.FALSE, children);
		super.setChildren(children);
		hasHashCode = false;
	}

	@Override
	public void addChild(int index, Expression newChild) {
		ensureSharedVariableMap(Compose.FALSE, newChild);
		super.addChild(index, newChild);
		hasHashCode = false;
	}

	@Override
	public void addChild(Expression newChild) {
		ensureSharedVariableMap(Compose.FALSE, newChild);
		super.addChild(newChild);
		hasHashCode = false;
	}

	@Override
	public void removeChild(Expression child) {
		super.removeChild(child);
		hasHashCode = false;
	}

	@Override
	public Expression removeChild(int index) {
		Expression expression = super.removeChild(index);
		hasHashCode = false;
		return expression;
	}

	@Override
	public void replaceChild(Expression oldChild, Expression newChild) {
		ensureSharedVariableMap(Compose.FALSE, newChild);
		super.replaceChild(oldChild, newChild);
		hasHashCode = false;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			int tempHashCode = computeHashCode();
			for (final Expression child : children) {
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
	public boolean equals(Object other) {
		return (other instanceof NonTerminal) && Trees.equals(this, (NonTerminal) other);
	}

	@Override
	public boolean equalsNode(Object other) {
		return (getClass() == other.getClass()) && (children.size() == ((NonTerminal) other).children.size());
	}

	@Override
	public String toString() {
		if (hasChildren()) {
			final StringBuilder sb = new StringBuilder();
			sb.append(getName());
			sb.append("[");
			for (final Expression child : children) {
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
