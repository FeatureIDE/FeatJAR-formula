package org.spldev.formulas.structure.atomic;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class LessThanInteger extends LessThan<Long> {

	public LessThanInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private LessThanInteger() {
		super();
	}

	@Override
	public LessThanInteger cloneNode() {
		return new LessThanInteger();
	}

	@Override
	public boolean eval(List<Long> values) {
		return (values.size() == 2) && (values.get(0) < values.get(1));
	}

}
