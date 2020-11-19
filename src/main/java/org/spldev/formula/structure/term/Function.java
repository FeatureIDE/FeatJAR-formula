package org.spldev.formula.structure.term;

import java.util.*;

import org.spldev.formula.structure.*;

public abstract class Function<R, T> extends NonTerminal implements Term<R> {

	public Function(Collection<Term<T>> nodes) {
		super(nodes);
	}

	@SafeVarargs
	public Function(Term<T>... nodes) {
		super(nodes);
	}

	public Function() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Term<T>> getChildren() {
		return (List<Term<T>>) super.getChildren();
	}

	public abstract Optional<T> eval(List<T> values);

}
