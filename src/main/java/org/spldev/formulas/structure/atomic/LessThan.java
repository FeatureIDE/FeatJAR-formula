package org.spldev.formulas.structure.atomic;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public abstract class LessThan<D> extends Predicate<D> {

	public LessThan(Term<D> leftArgument, Term<D> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected LessThan() {
		super();
	}

	@Override
	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "<";
	}

}
