package org.sk.prop4j.structure;

import java.util.*;

import org.sk.trees.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public interface Expression extends Tree {

	String getName();

	@Override
	List<? extends Expression> getChildren();

	@Override
	Expression clone();

}
