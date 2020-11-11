package org.spldev.formulas.structure.compound;

import java.util.*;

import org.spldev.formulas.structure.*;

/**
 * A logical connector that is {@code true} iff at most a given number of its
 * children are {@code true}.
 *
 * @author Sebastian Krieter
 */
public class AtMost extends Cardinal {

	public AtMost(List<Formula> nodes, int max) {
		super(nodes, 0, max);
	}

	private AtMost(AtMost oldNode) {
		super(oldNode);
	}

	@Override
	public AtMost cloneNode() {
		return new AtMost(this);
	}

	@Override
	public String getName() {
		return "atMost-" + max;
	}

	@Override
	public void setMax(int max) {
		super.setMax(max);
	}

}
