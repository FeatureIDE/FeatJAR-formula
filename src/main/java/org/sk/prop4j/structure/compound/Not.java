package org.sk.prop4j.structure.compound;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff its child is {@code false}.
 *
 * @author Sebastian Krieter
 */
public class Not extends NonTerminal<Formula> implements Connective {

	public Not(Formula node) {
		super(node);
	}

	private Not(Not oldNode) {
		super(oldNode);
	}

	@Override
	public Not clone() {
		return new Not(this);
	}

	@Override
	public String getName() {
		return "not";
	}

}
