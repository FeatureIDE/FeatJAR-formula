/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression;

import java.util.*;

import org.spldev.util.tree.*;
import org.spldev.util.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public abstract class NonTerminal extends AbstractNonTerminal<Expression> implements Expression {

	private int hashCode = 0;
	private boolean hasHashCode = false;

	protected NonTerminal() {
		super();
	}

	@SafeVarargs
	protected NonTerminal(Expression... children) {
		super();
		setChildren(Arrays.asList(children));
	}

	protected NonTerminal(Collection<? extends Expression> children) {
		super();
		setChildren(children);
	}

	public void setChildren(Collection<? extends Expression> children) {
		super.setChildren(children);
		hasHashCode = false;
	}

	@Override
	public final int hashCode() {
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
	public final boolean equals(Object other) {
		return (other instanceof NonTerminal) && Trees.equals(this, (NonTerminal) other);
	}

	@Override
	public boolean equalsNode(Object other) {
		return (getClass() == other.getClass()) && (children.size() == ((NonTerminal) other).children.size());
	}

	@Override
	public String toString() {
		if (hasChildren()) {
			StringBuilder sb = new StringBuilder();
			sb.append(getName());
			sb.append("[");
			for (Expression child : children) {
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
