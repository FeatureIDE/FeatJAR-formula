package org.sk.prop4j.structure.compound;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff the left child is {@code false}
 * or the right child is {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Implies extends NonTerminal<Formula> implements Connective {

	public Implies(Formula leftNode, Formula rightNode) {
		super(Arrays.asList(leftNode, rightNode));
	}

	private Implies(Implies oldNode) {
		super(oldNode);
	}

	@Override
	public Implies clone() {
		return new Implies(this);
	}

	@Override
	public String getName() {
		return "implies";
	}

}
