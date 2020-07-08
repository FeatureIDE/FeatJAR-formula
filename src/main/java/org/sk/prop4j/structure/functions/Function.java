package org.sk.prop4j.structure.functions;

import java.util.*;

import org.sk.prop4j.structure.*;
import org.sk.trees.structure.*;

public interface Function<R, T> extends Term<R>, NonTerminalNode<Term<T>> {

	@Override
	List<Term<T>> getChildren();

	T eval(List<T> values);

}
