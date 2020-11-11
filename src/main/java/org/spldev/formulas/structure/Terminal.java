package org.spldev.formulas.structure;

import java.util.*;

import org.spldev.trees.structure.*;

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
		return Objects.hashCode(getName());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		return Objects.equals(getName(), ((Terminal) other).getName());
	}

//	@Override
//	public Terminal clone() {
//		throw new IllegalStateException();
//	}

//	@Override
//	public List<? extends Expression> getChildren() {
//		return Collections.emptyList();
//	}

	@Override
	public String toString() {
		return getName();
	}

}
