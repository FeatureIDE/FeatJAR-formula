package org.spldev.formula.expression.atomic.literal;

import java.util.*;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public class LiteralVariable extends Literal {

	// TODO replace with id to decrease memory footprint
	protected String name;
	protected boolean positive;

	private boolean hasHashCode;
	private int hashCode;

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
		final LiteralVariable literalVariable = new LiteralVariable(name, positive);
		literalVariable.hasHashCode = hasHashCode;
		literalVariable.hashCode = hashCode;
		return literalVariable;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			hashCode = Objects.hashCode(name) + (positive ? 31 : 37);
			hasHashCode = true;
		}
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final LiteralVariable otherLiteralVariable = (LiteralVariable) other;
		return (positive == otherLiteralVariable.positive) &&
			Objects.equals(name, otherLiteralVariable.name);
	}

	@Override
	public String toString() {
		return (positive ? "+" : "-") + name;
	}

}
