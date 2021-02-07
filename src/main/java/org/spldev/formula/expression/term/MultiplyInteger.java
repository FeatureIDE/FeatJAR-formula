package org.spldev.formula.expression.term;

import java.util.*;

public class MultiplyInteger extends Multiply<Long> {

	public MultiplyInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private MultiplyInteger() {
		super();
	}

	@Override
	public MultiplyInteger cloneNode() {
		return new MultiplyInteger();
	}

	@Override
	public Optional<Long> eval(List<Long> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return values.stream().reduce((a, b) -> a * b);
	}

}
