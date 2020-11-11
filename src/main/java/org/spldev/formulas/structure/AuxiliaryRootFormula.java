package org.spldev.formulas.structure;

import org.spldev.trees.structure.*;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public class AuxiliaryRootFormula extends NonTerminal implements Expression {

	public AuxiliaryRootFormula(Expression node) {
		super(node);
	}

	private AuxiliaryRootFormula() {
		super();
	}

	@Override
	public String getName() {
		return "";
	}

	public Expression getChild() {
		return getChildren().iterator().next();
	}

	@Override
	public Tree<Expression> cloneNode() {
		return new AuxiliaryRootFormula();
	}

}
