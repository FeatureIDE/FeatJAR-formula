package org.spldev.formulas.structure.functions;

import java.util.*;

import org.spldev.formulas.structure.*;

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
	public Double eval(List<Double> values) {
		return values.stream().reduce(Double::sum).get();
	}

}
