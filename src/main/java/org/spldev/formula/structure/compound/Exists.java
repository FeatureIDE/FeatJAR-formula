package org.spldev.formula.structure.compound;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.term.*;

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
