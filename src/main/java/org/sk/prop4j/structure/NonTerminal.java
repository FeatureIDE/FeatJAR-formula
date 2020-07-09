package org.sk.prop4j.structure;

import java.util.*;

import org.sk.trees.structure.NonTerminalNode;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public abstract class NonTerminal<T extends Expression> implements Expression, NonTerminalNode<T> {

	protected final List<T> children;

	@SafeVarargs
	protected NonTerminal(T... children) {
		this.children = new ArrayList<>(children.length);
		setChildren(Arrays.asList(children));
	}

	protected NonTerminal(Collection<T> children) {
		this.children = new ArrayList<>(children.size());
		setChildren(children);
	}

	protected NonTerminal(NonTerminal<T> oldNode) {
		this.children = new ArrayList<>(oldNode.getChildren().size());
		setChildren(NonTerminalNode.cloneChildren(oldNode));
	}

	@Override
	public List<T> getChildren() {
		return children;
	}

	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		hashCode = (37 * hashCode) + Objects.hashCode(getName());
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		final NonTerminal<?> otherNode = (NonTerminal<?>) other;
		return Objects.equals(getName(), otherNode.getName()) && super.equals(other);
	}

	@Override
	public NonTerminal<T> clone() {
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return getName();
	}

}
