package org.sk.prop4j.structure.compound;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff all of its children are
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class And extends NonTerminal<Formula> implements Connective {

	public And(Collection<Formula> nodes) {
		super(nodes);
	}

	public And(Formula... nodes) {
		super(Arrays.asList(nodes));
	}

	private And(And oldNode) {
		super(oldNode);
	}

	@Override
	public And clone() {
		return new And(this);
	}

	@Override
	public String getName() {
		return "and";
	}

}
