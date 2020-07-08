package org.sk.prop4j.structure.functions;

import java.util.*;

import org.sk.prop4j.structure.*;

public abstract class Add<T> extends NonTerminal<Term<T>> implements Function<T, T> {

	public Add(Term<T> leftArgument, Term<T> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected Add(Add<T> oldNode) {
		super(oldNode);
	}

	public void setArguments(Term<T> leftArgument, Term<T> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "+";
	}

	@Override
	public abstract Add<T> clone();

}
