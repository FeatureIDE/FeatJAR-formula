package org.spldev.formula.expression.term;

import java.util.*;

public class AddInteger extends Add<Long> {

	public AddInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private AddInteger() {
		super();
	}

	@Override
	public AddInteger cloneNode() {
		return new AddInteger();
	}

	@Override
	public Optional<Long> eval(List<Long> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return values.stream().reduce(Long::sum);
	}

}
