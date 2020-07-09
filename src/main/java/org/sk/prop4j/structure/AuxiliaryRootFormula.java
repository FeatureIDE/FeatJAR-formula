package org.sk.prop4j.structure;

import org.sk.prop4j.structure.compound.Compound;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public class AuxiliaryRootFormula extends NonTerminal<Formula> implements Compound {

	public AuxiliaryRootFormula(Formula node) {
		super(node);
	}

	private AuxiliaryRootFormula(AuxiliaryRootFormula oldNode) {
		super(oldNode);
	}

	@Override
	public AuxiliaryRootFormula clone() {
		return new AuxiliaryRootFormula(this);
	}

	@Override
	public String getName() {
		return "";
	}

	public Formula getNode() {
		return getChildren().get(0);
	}

}
