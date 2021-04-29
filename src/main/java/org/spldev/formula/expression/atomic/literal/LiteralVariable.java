package org.spldev.formula.expression.atomic.literal;

import java.util.*;

import org.spldev.formula.*;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public class LiteralVariable extends Literal {

	private int value;
	private VariableMap map;

	public LiteralVariable(String name, VariableMap map) {
		this(name, map , true);
	}

	public LiteralVariable(String name, VariableMap map, boolean positive) {
		this.map = Objects.requireNonNull(map);
		int index = map.getIndex(name).orElse(0);
		if (index == 0) {
			throw new IllegalArgumentException(name);
		}
		this.value = positive ? index : -index;
	}

	public LiteralVariable(int value, VariableMap map) {
		this.map = Objects.requireNonNull(map);
		if (value == 0) {
			throw new IllegalArgumentException();
		}
		this.value = value;
	}

	@Override
	public String getName() {
		return map.getName(Math.abs(value)).orElse("??");
	}

	@Override
	public boolean isPositive() {
		return value > 0;
	}

	public void setPositive(boolean positive) {
		if (value > 0 != positive) {
			value = -value;
		}
	}

	@Override
	public LiteralVariable flip() {
		final LiteralVariable clonedNode = cloneNode();
		clonedNode.value = -clonedNode.value;
		return clonedNode;
	}

	@Override
	public LiteralVariable cloneNode() {
		return new LiteralVariable(value, map);
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final LiteralVariable otherLiteralVariable = (LiteralVariable) other;
		return (value == otherLiteralVariable.value);
	}

	@Override
	public String toString() {
		return (value > 0 ? "+" : "-") + getName();
	}

}
