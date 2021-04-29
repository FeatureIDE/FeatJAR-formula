package org.spldev.formula.expression;

import java.util.*;

import org.spldev.util.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Thomas Thuem
 * @author Marcus Pinnecke (Feature Interface)
 */
public abstract class Terminal extends AbstractTerminal<Expression> implements Expression {

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), getName());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		return equalsNode(other);
	}

	@Override
	public boolean equalsNode(Object other) {
		return (getClass() == other.getClass())
			&& Objects.equals(getName(), ((Terminal) other).getName());
	}

	@Override
	public String toString() {
		return getName();
	}

}
