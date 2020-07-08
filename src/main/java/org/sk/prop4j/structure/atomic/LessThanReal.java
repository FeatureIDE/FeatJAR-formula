package org.sk.prop4j.structure.atomic;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class LessThanReal extends LessThan<Double> {

	public LessThanReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private LessThanReal(LessThanReal oldNode) {
		super(oldNode);
	}

	@Override
	public LessThanReal clone() {
		return new LessThanReal(this);
	}

	@Override
	public boolean eval(List<Double> values) {
		return (values.size() == 2) && (values.get(0) < values.get(1));
	}

}
