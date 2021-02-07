package org.spldev.formula.expression.term;

public abstract class Multiply<T> extends Function<T, T> {

	public Multiply(Term<T> leftArgument, Term<T> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected Multiply() {
		super();
	}

	@Override
	public String getName() {
		return "*";
	}

	@Override
	public abstract Multiply<T> cloneNode();

}
