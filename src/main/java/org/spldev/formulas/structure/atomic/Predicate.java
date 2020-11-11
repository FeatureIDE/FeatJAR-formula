package org.spldev.formulas.structure.atomic;

import java.util.*;

import org.spldev.formulas.structure.*;

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

	public abstract boolean eval(List<D> values);

}
