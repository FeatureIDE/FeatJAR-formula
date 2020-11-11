package org.spldev.formulas.structure.functions;

import java.util.*;

import org.spldev.formulas.structure.*;

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
	public Long eval(List<Long> values) {
		return values.stream().reduce((a, b) -> a * b).get();
	}

}
