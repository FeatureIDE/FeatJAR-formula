package org.sk.prop4j.structure.atomic;

import java.util.Arrays;

import org.sk.prop4j.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public abstract class Equal<T> extends NonTerminal<Term<T>> implements Predicate<T> {

	public Equal(Term<T> leftArgument, Term<T> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected Equal(Equal<T> oldNode) {
		super(oldNode);
	}

	public void setArguments(Term<T> leftArgument, Term<T> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "=";
	}

	@Override
	public abstract Equal<T> clone();

}
