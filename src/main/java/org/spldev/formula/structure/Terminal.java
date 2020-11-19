package org.spldev.formula.structure;

import java.util.*;

import org.spldev.tree.structure.*;

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
		return Objects.hash(getClass().getCanonicalName(), getName());
	}

	@Override
	public boolean equals(Object other) {
		return equalsNode(other);
	}

	@Override
	public boolean equalsNode(Object other) {
		if (this == other) {
			return true;
		}
		if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		return Objects.equals(getName(), ((Terminal) other).getName());
	}

	@Override
	public String toString() {
		return getName();
	}

}
