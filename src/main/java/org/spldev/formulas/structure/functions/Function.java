package org.spldev.formulas.structure.functions;

import java.util.*;

import org.spldev.formulas.structure.*;

public abstract class Function<R, T> extends NonTerminal implements Term<R> {

//	@Override
//	List<Term<T>> getChildren();
//
//	
//	@Override
//	List<? extends Term<T>> getChildren();
//	

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

	public abstract T eval(List<T> values);

}
