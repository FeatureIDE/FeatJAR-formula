package org.sk.prop4j.structure.functions;

import java.util.*;

import org.sk.prop4j.structure.*;

public class MultiplyReal extends Multiply<Double> {

	public MultiplyReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private MultiplyReal(MultiplyReal oldNode) {
		super(oldNode);
	}

	@Override
	public MultiplyReal clone() {
		return new MultiplyReal(this);
	}

	@Override
	public Double eval(List<Double> values) {
		return values.stream().reduce((a, b) -> a * b).get();
	}

}
