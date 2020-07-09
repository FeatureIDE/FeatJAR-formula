package org.sk.prop4j.structure;

import java.util.*;

import org.sk.trees.structure.TerminalNode;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Thomas Thuem
 * @author Marcus Pinnecke (Feature Interface)
 */
public abstract class Terminal implements Expression, TerminalNode {

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
		final Terminal otherNode = (Terminal) other;
		return Objects.equals(getName(), otherNode.getName());
	}

	@Override
	public Terminal clone() {
		throw new IllegalStateException();
	}

	@Override
	public List<? extends Expression> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return getName();
	}

}
