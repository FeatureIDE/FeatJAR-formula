package org.sk.prop4j.structure.atomic;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.trees.structure.*;

public interface Predicate<T> extends Atomic, NonTerminalNode<Term<T>> {

	@Override
	List<Term<T>> getChildren();

	boolean eval(List<T> values);

}
