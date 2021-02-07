package org.spldev.formula.expression.atomic.predicate;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.term.*;

public abstract class Predicate<D> extends NonTerminal implements Atomic {

	protected Predicate(Collection<Term<D>> nodes) {
		super(nodes);
	}

	@SafeVarargs
	protected Predicate(Term<D>... nodes) {
		super(nodes);
	}

	protected Predicate() {
		super();
	}

	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "=";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Term<D>> getChildren() {
		return (List<? extends Term<D>>) super.getChildren();
	}

	public abstract Optional<Boolean> eval(List<D> values);

	@Override
	public abstract Predicate<D> flip();
}
