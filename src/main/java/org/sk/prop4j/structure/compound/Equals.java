package org.sk.prop4j.structure.compound;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff the left child has the same
 * value as the right child.
 *
 * @author Sebastian Krieter
 */
public class Equals extends NonTerminal<Formula> implements Connective {

	public Equals(Formula leftNode, Formula rightNode) {
		super(Arrays.asList(leftNode, rightNode));
	}

	private Equals(Equals oldNode) {
		super(oldNode);
	}

	@Override
	public Equals clone() {
		return new Equals(this);
	}

	@Override
	public String getName() {
		return "equals";
	}

}
