package org.spldev.formula.expression.term;

import java.util.*;

public class AddReal extends Add<Double> {

	public AddReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private AddReal() {
		super();
	}

	@Override
	public AddReal cloneNode() {
		return new AddReal();
	}

	@Override
	public Optional<Double> eval(List<Double> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return values.stream().reduce(Double::sum);
	}

}
