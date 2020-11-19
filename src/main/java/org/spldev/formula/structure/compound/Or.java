package org.spldev.formula.structure.compound;

import java.util.*;

import org.spldev.formula.structure.*;

/**
 * A logical connector that is {@code true} iff at least one of its children is
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Or extends Compound implements Connective {

	public Or(Collection<Formula> nodes) {
		super(nodes);
	}

	public Or(Formula... nodes) {
		super(nodes);
	}

	private Or() {
		super();
	}

	@Override
	public Or cloneNode() {
		return new Or();
	}

	@Override
	public String getName() {
		return "or";
	}

}
