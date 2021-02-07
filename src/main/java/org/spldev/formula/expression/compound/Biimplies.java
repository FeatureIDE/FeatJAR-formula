package org.spldev.formula.expression.compound;

import org.spldev.formula.expression.*;

/**
 * A logical connector that is {@code true} iff the left child has the same
 * value as the right child.
 *
 * @author Sebastian Krieter
 */
public class Biimplies extends Compound implements Connective {

	public Biimplies(Formula leftNode, Formula rightNode) {
		super(leftNode, rightNode);
	}

	private Biimplies() {
		super();
	}

	@Override
	public Biimplies cloneNode() {
		return new Biimplies();
	}

	@Override
	public String getName() {
		return "biimplies";
	}

}
