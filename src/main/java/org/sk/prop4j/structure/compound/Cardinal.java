package org.sk.prop4j.structure.compound;

import java.util.List;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff the number of its children that
 * are {@code true} is larger or equal to a lower bound and smaller or equal to
 * an upper bound.
 *
 * @author Sebastian Krieter
 */
public abstract class Cardinal extends NonTerminal<Formula> implements Connective {

	protected int min;
	protected int max;

	public Cardinal(List<Formula> nodes, int min, int max) {
		super(nodes);
		setMin(min);
		setMax(max);
	}

	public Cardinal(Cardinal oldNode) {
		super(oldNode);
		setMin(oldNode.min);
		setMax(oldNode.max);
	}

	@Override
	public Cardinal clone() {
		throw new IllegalStateException();
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	protected void setMin(int min) {
		if ((min < 0) || (min > children.size())) {
			throw new IllegalArgumentException(Integer.toString(min));
		}
		this.min = min;
	}

	protected void setMax(int max) {
		if ((max < 0) || (max > children.size())) {
			throw new IllegalArgumentException(Integer.toString(max));
		}
		this.max = max;
	}

	public void setChildren(List<Formula> children) {
		if (min > children.size()) {
			throw new IllegalArgumentException(Integer.toString(min));
		}
		if (max > children.size()) {
			throw new IllegalArgumentException(Integer.toString(max));
		}
		super.setChildren(children);
	}

	@Override
	public int hashCode() {
		int hashCode = min;
		hashCode = (37 * hashCode) + max;
		hashCode = (37 * hashCode) + super.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		final Cardinal otherCardinal = (Cardinal) other;
		return (min == otherCardinal.min) && (max == otherCardinal.max);
	}

}
