/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.compound;

import java.util.*;

import org.spldev.formula.expression.*;

/**
 * A logical connector that is {@code true} iff the number of its children that
 * are {@code true} is larger or equal to a lower bound and smaller or equal to
 * an upper bound.
 *
 * @author Sebastian Krieter
 */
public abstract class Cardinal extends Compound {

	protected int min;
	protected int max;

	public Cardinal(List<Formula> nodes, int min, int max) {
		super(nodes);
		setMin(min);
		setMax(max);
	}

	public Cardinal(Cardinal oldNode) {
		super();
		setMin(oldNode.min);
		setMax(oldNode.max);
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	protected void setMin(int min) {
		if (min < 0) {
			throw new IllegalArgumentException(Integer.toString(min));
		}
		this.min = min;
	}

	protected void setMax(int max) {
		if (max < 0) {
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
	protected int computeHashCode() {
		int hashCode = super.computeHashCode();
		hashCode = (37 * hashCode) + min;
		hashCode = (37 * hashCode) + max;
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (!super.equalsNode(other)) {
			return false;
		}
		final Cardinal otherCardinal = (Cardinal) other;
		return (min == otherCardinal.min) && (max == otherCardinal.max);
	}

}
