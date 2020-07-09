package org.sk.prop4j.structure.atomic;

import java.util.Comparator;

/**
 * Compares two literals based on their {@code var} object and {@code positive}
 * state.
 *
 * @author Sebastian Krieter
 */
public class LiteralComparator implements Comparator<Literal> {

	@Override
	public int compare(Literal arg0, Literal arg1) {
		if (arg0.positive == arg1.positive) {
			final Object nameObject0 = arg0.getNameObject();
			final Object nameObject1 = arg1.getNameObject();
			final Class<? extends Object> class0 = nameObject0.getClass();
			final Class<? extends Object> class1 = nameObject1.getClass();
			if (class0 == class1) {
				return nameObject0.toString().compareTo(nameObject1.toString());
			} else {
				return class0.getCanonicalName().compareTo(class1.getCanonicalName());
			}
		} else {
			return arg0.positive ? -1 : 1;
		}
	}

}
