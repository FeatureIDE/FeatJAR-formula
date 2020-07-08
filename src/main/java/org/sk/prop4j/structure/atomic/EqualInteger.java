package org.sk.prop4j.structure.atomic;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class EqualInteger extends Equal<Long> {

	public EqualInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private EqualInteger(EqualInteger oldNode) {
		super(oldNode);
	}

	@Override
	public EqualInteger clone() {
		return new EqualInteger(this);
	}

	@Override
	public boolean eval(List<Long> values) {
		return (values.size() == 2) && (values.get(0) == values.get(1));
	}

}
