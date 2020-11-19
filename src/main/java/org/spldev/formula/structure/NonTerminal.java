package org.spldev.formula.structure;

import java.util.*;

import org.spldev.tree.*;
import org.spldev.tree.structure.*;

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

	@Override
	public final int hashCode() {
		if (!hasHashCode) {
			int tempHashCode = computeHashCode();
			for (final Expression child : children) {
				tempHashCode = (tempHashCode * 37) + child.hashCode();
			}
			hashCode = tempHashCode;
			hasHashCode = true;
		}
		return hashCode;
	}

	protected int computeHashCode() {
		return Objects.hash(getClass().getCanonicalName(), getName(), getChildren().size());
	}

	@Override
	public final boolean equals(Object other) {
		return (other instanceof NonTerminal) && Trees.equals(this, (NonTerminal) other);
	}

	@Override
	public boolean equalsNode(Object other) {
		if (this == other) {
			return true;
		}
		if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		final NonTerminal nonTerminal = (NonTerminal) other;
		return (getChildren().size() == nonTerminal.getChildren().size())
			&& Objects.equals(getName(), nonTerminal.getName());
	}

	@Override
	public String toString() {
		return getName();
	}

}
