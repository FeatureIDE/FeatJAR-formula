package org.spldev.formulas.structure.atomic;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class EqualReal extends Equal<Double> {

	public EqualReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private EqualReal() {
		super();
	}

	@Override
	public EqualReal cloneNode() {
		return new EqualReal();
	}

	@Override
	public boolean eval(List<Double> values) {
		return (values.size() == 2) && (values.get(0) == values.get(1));
	}

}
