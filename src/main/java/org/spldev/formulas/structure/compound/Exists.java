package org.spldev.formulas.structure.compound;

import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.terms.*;

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
