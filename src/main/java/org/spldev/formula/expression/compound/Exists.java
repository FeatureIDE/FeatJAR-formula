package org.spldev.formula.expression.compound;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.term.*;

public class Exists extends Quantifier {

	public Exists(Variable<?> boundVariable, Formula formula) {
		super(boundVariable, formula);
	}

	private Exists(Exists oldNode) {
		super(oldNode);
	}

	@Override
	public String getName() {
		return "exists";
	}

	@Override
	public Exists cloneNode() {
		return new Exists(this);
	}

}
