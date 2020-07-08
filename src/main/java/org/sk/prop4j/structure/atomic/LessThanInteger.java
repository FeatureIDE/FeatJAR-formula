package org.sk.prop4j.structure.atomic;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class LessThanInteger extends LessThan<Long> {

	public LessThanInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private LessThanInteger(LessThanInteger oldNode) {
		super(oldNode);
	}

	@Override
	public LessThanInteger clone() {
		return new LessThanInteger(this);
	}

	@Override
	public boolean eval(List<Long> values) {
		return (values.size() == 2) && (values.get(0) < values.get(1));
	}

}
