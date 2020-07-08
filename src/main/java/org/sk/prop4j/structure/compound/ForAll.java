package org.sk.prop4j.structure.compound;

import org.sk.prop4j.structure.*;
import org.sk.prop4j.structure.terms.*;

public class ForAll extends Quantifier {

	public ForAll(Variable<?> boundVariable, Formula formula) {
		super(boundVariable, formula);
	}

	public ForAll(ForAll oldNode) {
		super(oldNode);
	}

	@Override
	public String getName() {
		return "for all";
	}

	@Override
	public ForAll clone() {
		return new ForAll(this);
	}

}
