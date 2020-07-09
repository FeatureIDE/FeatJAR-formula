package org.sk.prop4j.structure.functions;

import java.util.List;

import org.sk.prop4j.structure.Term;

public class AddInteger extends Add<Long> {

	public AddInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private AddInteger(AddInteger oldNode) {
		super(oldNode);
	}

	@Override
	public AddInteger clone() {
		return new AddInteger(this);
	}

	@Override
	public Long eval(List<Long> values) {
		return values.stream().reduce(Long::sum).get();
	}

}
