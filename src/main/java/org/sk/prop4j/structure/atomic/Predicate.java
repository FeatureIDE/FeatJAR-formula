package org.sk.prop4j.structure.atomic;

import java.util.List;

import org.sk.prop4j.structure.Term;
import org.sk.trees.structure.NonTerminalNode;

public interface Predicate<T> extends Atomic, NonTerminalNode<Term<T>> {

	@Override
	List<Term<T>> getChildren();

	boolean eval(List<T> values);

}
