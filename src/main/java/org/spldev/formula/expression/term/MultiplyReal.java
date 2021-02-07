package org.spldev.formula.expression.term;

import java.util.*;

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
	public Optional<Double> eval(List<Double> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return values.stream().reduce((a, b) -> a * b);
	}

}
