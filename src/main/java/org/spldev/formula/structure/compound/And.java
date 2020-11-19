package org.spldev.formula.structure.compound;

import java.util.*;

import org.spldev.formula.structure.*;

/**
 * A logical connector that is {@code true} iff all of its children are
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class And extends Compound implements Connective {

	public And(Collection<Formula> nodes) {
		super(nodes);
	}

	public And(Formula... nodes) {
		super(nodes);
	}

	private And() {
		super();
	}

	@Override
	public And cloneNode() {
		return new And();
	}

	@Override
	public String getName() {
		return "and";
	}

}
