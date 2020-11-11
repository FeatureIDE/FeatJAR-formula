package org.spldev.formulas.structure.atomic;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class EqualInteger extends Equal<Long> {

	public EqualInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private EqualInteger() {
		super();
	}

	@Override
	public EqualInteger cloneNode() {
		return new EqualInteger();
	}

	@Override
	public boolean eval(List<Long> values) {
		return (values.size() == 2) && (values.get(0) == values.get(1));
	}

}
