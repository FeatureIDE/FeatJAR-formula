package org.sk.prop4j.structure.functions;

import org.sk.prop4j.structure.*;

public abstract class Multiply<T> extends NonTerminal<Term<T>> implements Function<T, T> {

	public Multiply(Term<T> leftArgument, Term<T> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected Multiply(Multiply<T> oldNode) {
		super(oldNode);
	}

	@Override
	public String getName() {
		return "*";
	}

	@Override
	public abstract Multiply<T> clone();

}
