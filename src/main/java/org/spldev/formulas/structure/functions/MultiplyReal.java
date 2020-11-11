package org.spldev.formulas.structure.functions;

import java.util.*;

import org.spldev.formulas.structure.*;

public class MultiplyReal extends Multiply<Double> {

	public MultiplyReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private MultiplyReal() {
		super();
	}

	@Override
	public MultiplyReal cloneNode() {
		return new MultiplyReal();
	}

	@Override
	public Double eval(List<Double> values) {
		return values.stream().reduce((a, b) -> a * b).get();
	}

}
