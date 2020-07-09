package org.sk.prop4j.structure.functions;

import java.util.List;

import org.sk.prop4j.structure.Term;

public class MultiplyInteger extends Multiply<Long> {

	public MultiplyInteger(Term<Long> leftArgument, Term<Long> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private MultiplyInteger(MultiplyInteger oldNode) {
		super(oldNode);
	}

	@Override
	public MultiplyInteger clone() {
		return new MultiplyInteger(this);
	}

	@Override
	public Long eval(List<Long> values) {
		return values.stream().reduce((a, b) -> a * b).get();
	}

}
