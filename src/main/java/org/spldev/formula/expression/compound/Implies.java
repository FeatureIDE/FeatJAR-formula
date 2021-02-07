package org.spldev.formula.expression.compound;

import org.spldev.formula.expression.*;

/**
 * A logical connector that is {@code true} iff the left child is {@code false}
 * or the right child is {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Implies extends Compound implements Connective {

	public Implies(Formula leftNode, Formula rightNode) {
		super(leftNode, rightNode);
	}

	private Implies() {
		super();
	}

	@Override
	public Implies cloneNode() {
		return new Implies();
	}

	@Override
	public String getName() {
		return "implies";
	}

}
