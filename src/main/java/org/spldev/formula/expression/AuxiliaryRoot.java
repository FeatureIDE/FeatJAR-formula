package org.spldev.formula.expression;

import java.util.*;

import org.spldev.tree.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public class AuxiliaryRoot extends NonTerminal {

	public AuxiliaryRoot(Expression node) {
		super(node);
	}

	private AuxiliaryRoot() {
		super();
	}

	@Override
	public String getName() {
		return "";
	}

	public Expression getChild() {
		return getChildren().iterator().next();
	}

	public void setChild(Expression node) {
		Objects.requireNonNull(node);
		setChildren(Arrays.asList(node));
	}

	@Override
	public Tree<Expression> cloneNode() {
		return new AuxiliaryRoot();
	}

}
