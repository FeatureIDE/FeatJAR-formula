package org.spldev.formulas.structure;

import java.util.*;

import org.spldev.trees.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public abstract class NonTerminal extends AbstractNonTerminal<Expression> implements Expression {

//	protected final List<NonTerminal> children;

	protected NonTerminal() {
		super();
	}

	@SafeVarargs
	protected NonTerminal(Expression... children) {
		super();
//		this.children = new ArrayList<>(children.length);
		setChildren(Arrays.asList(children));
	}

	protected NonTerminal(Collection<? extends Expression> children) {
		super();
//		this.children = new ArrayList<>(children.size());
		setChildren(children);
	}

//	protected NonTerminal(NonTerminal oldNode) {
//		this.children = new ArrayList<>(oldNode.getChildren().size());
//		setChildren(NonTerminalNode.cloneChildren(oldNode));
//	}

//	@Override
//	public List<T> getChildren() {
//		return children;
//	}

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
		return Objects.equals(getName(), ((NonTerminal) other).getName());
	}

//	@Override
//	public NonTerminal<T> clone() {
//		throw new IllegalStateException();
//	}

	@Override
	public String toString() {
		return getName();
	}

}
