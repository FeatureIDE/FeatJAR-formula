package org.spldev.formula.expression.term;

import java.util.*;

public abstract class Add<T> extends Function<T, T> {

	public Add(Term<T> leftArgument, Term<T> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected Add() {
		super();
	}

	public void setArguments(Term<T> leftArgument, Term<T> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "+";
	}

	@Override
	public abstract Add<T> cloneNode();

}
