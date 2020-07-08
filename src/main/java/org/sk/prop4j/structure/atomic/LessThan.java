package org.sk.prop4j.structure.atomic;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public abstract class LessThan<T> extends NonTerminal<Term<T>> implements Predicate<T> {

	public LessThan(Term<T> leftArgument, Term<T> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected LessThan(LessThan<T> oldNode) {
		super(oldNode);
	}

	public void setArguments(Term<T> leftArgument, Term<T> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "<";
	}

	@Override
	public abstract LessThan<T> clone();

}
