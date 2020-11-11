package org.spldev.formulas.structure.functions;

import java.util.*;

import org.spldev.formulas.structure.*;

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
	public Long eval(List<Long> values) {
		return values.stream().reduce(Long::sum).get();
	}

}
