package org.sk.prop4j.structure.functions;

import java.util.*;

import org.sk.prop4j.structure.*;

public class AddReal extends Add<Double> {

	public AddReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private AddReal(AddReal oldNode) {
		super(oldNode);
	}

	@Override
	public AddReal clone() {
		return new AddReal(this);
	}

	@Override
	public Double eval(List<Double> values) {
		return values.stream().reduce(Double::sum).get();
	}

}
