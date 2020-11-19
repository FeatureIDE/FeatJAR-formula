package org.spldev.formula.structure.atomic.literal;

import java.util.*;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public class LiteralVariable extends Literal {

	protected boolean positive;

	protected String name;

	public LiteralVariable(String name) {
		this(name, true);
	}

	public LiteralVariable(String name, boolean positive) {
		Objects.requireNonNull(name);
		this.name = name;
		this.positive = positive;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	@Override
	public LiteralVariable flip() {
		final LiteralVariable clonedNode = cloneNode();
		clonedNode.positive = !clonedNode.positive;
		return clonedNode;
	}

	@Override
	public LiteralVariable cloneNode() {
		return new LiteralVariable(name, positive);
	}

	@Override
	public int hashCode() {
		int hashCode = positive ? 31 : 37;
		hashCode = (37 * hashCode) + super.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		return equalsNode(other);
	}

	@Override
	public boolean equalsNode(Object other) {
		if (!super.equalsNode(other)) {
			return false;
		}
		return positive == ((LiteralVariable) other).positive;
	}

	@Override
	public String toString() {
		return (positive ? "+" : "-") + getName();
	}

}
