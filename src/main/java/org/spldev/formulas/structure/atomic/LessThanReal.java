package org.spldev.formulas.structure.atomic;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class LessThanReal extends LessThan<Double> {

	public LessThanReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private LessThanReal() {
		super();
	}

	@Override
	public LessThanReal cloneNode() {
		return new LessThanReal();
	}

	@Override
	public boolean eval(List<Double> values) {
		return (values.size() == 2) && (values.get(0) < values.get(1));
	}

}
