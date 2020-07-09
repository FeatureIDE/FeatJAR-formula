package org.sk.prop4j.structure.compound;

import org.sk.prop4j.structure.*;

public class Exists extends Quantifier {

	public Exists(Variable<?> boundVariable, Formula formula) {
		super(boundVariable, formula);
	}

	public Exists(Exists oldNode) {
		super(oldNode);
	}

	@Override
	public String getName() {
		return "exists";
	}

	@Override
	public Exists clone() {
		return new Exists(this);
	}

}
