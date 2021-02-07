package org.spldev.formula.expression.compound;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.term.*;

public class ForAll extends Quantifier {

	public ForAll(Variable<?> boundVariable, Formula formula) {
		super(boundVariable, formula);
	}

	private ForAll(ForAll oldNode) {
		super(oldNode);
	}

	@Override
	public String getName() {
		return "for all";
	}

	@Override
	public ForAll cloneNode() {
		return new ForAll(this);
	}

}
