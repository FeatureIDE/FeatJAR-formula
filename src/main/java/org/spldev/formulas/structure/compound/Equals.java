package org.spldev.formulas.structure.compound;

import org.spldev.formulas.structure.*;

/**
 * A logical connector that is {@code true} iff the left child has the same
 * value as the right child.
 *
 * @author Sebastian Krieter
 */
public class Equals extends Compound implements Connective {

	public Equals(Formula leftNode, Formula rightNode) {
		super(leftNode, rightNode);
	}

	private Equals() {
		super();
	}

	@Override
	public Equals cloneNode() {
		return new Equals();
	}

	@Override
	public String getName() {
		return "equals";
	}

}
