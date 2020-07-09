package org.sk.prop4j.structure;

import java.util.List;

import org.sk.trees.structure.Tree;

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
