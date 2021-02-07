package org.spldev.formula.expression.atomic.literal;

import java.util.*;

/**
 * Compares two literals based on their {@code var} object and {@code positive}
 * state.
 *
 * @author Sebastian Krieter
 */
public class LiteralComparator implements Comparator<Literal> {

	@Override
	public int compare(Literal arg0, Literal arg1) {
		if (arg0.isPositive() != arg1.isPositive()) {
			return arg0.isPositive() ? -1 : 1;
		}
		final int nameCompare = arg0.getName().compareTo(arg1.getName());
		if (nameCompare != 0) {
			return nameCompare;
		}
		return arg0.getClass().getCanonicalName().compareTo(arg1.getClass().getCanonicalName());
	}

}
