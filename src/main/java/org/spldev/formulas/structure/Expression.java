package org.spldev.formulas.structure;

import java.util.*;

import org.spldev.trees.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public interface Expression extends Tree<Expression> {

	String getName();

//	@Override
	@Override
	List<? extends Expression> getChildren();

//	@Override
//	Expression clone();

}
