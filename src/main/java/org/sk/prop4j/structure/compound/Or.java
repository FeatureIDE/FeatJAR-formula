package org.sk.prop4j.structure.compound;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff at least one of its children is
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Or extends NonTerminal<Formula> implements Connective {

	public Or(Collection<Formula> nodes) {
		super(nodes);
	}

	public Or(Formula... nodes) {
		super(Arrays.asList(nodes));
	}

	private Or(Or oldNode) {
		super(oldNode);
	}

	@Override
	public Or clone() {
		return new Or(this);
	}

	@Override
	public String getName() {
		return "or";
	}

}
