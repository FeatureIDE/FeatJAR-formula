package org.spldev.formula.structure.compound;

import org.spldev.formula.structure.*;

/**
 * A logical connector that is {@code true} iff its child is {@code false}.
 *
 * @author Sebastian Krieter
 */
public class Not extends Compound implements Connective {

	public Not(Formula node) {
		super(node);
	}

	private Not() {
		super();
	}

	@Override
	public Not cloneNode() {
		return new Not();
	}

	@Override
	public String getName() {
		return "not";
	}

}
