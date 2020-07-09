package org.sk.prop4j.structure.functions;

import java.util.List;

import org.sk.prop4j.structure.Term;
import org.sk.trees.structure.NonTerminalNode;

public interface Function<R, T> extends Term<R>, NonTerminalNode<Term<T>> {

	@Override
	List<Term<T>> getChildren();

	T eval(List<T> values);

}
