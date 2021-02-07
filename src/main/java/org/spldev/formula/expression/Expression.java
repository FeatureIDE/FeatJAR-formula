package org.spldev.formula.expression;

import java.util.*;

import org.spldev.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public interface Expression extends Tree<Expression> {

	String getName();

	@Override
	List<? extends Expression> getChildren();

}
