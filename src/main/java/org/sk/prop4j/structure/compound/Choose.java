package org.sk.prop4j.structure.compound;

import java.util.*;

import org.sk.prop4j.structure.*;

/**
 * A logical connector that is {@code true} iff the number of its children that
 * are {@code true} is equal to a given number.
 *
 * @author Sebastian Krieter
 */
public class Choose extends Cardinal {

	public Choose(List<Formula> nodes, int k) {
		super(nodes, k, k);
	}

	private Choose(Choose oldNode) {
		super(oldNode);
	}

	@Override
	public Choose clone() {
		return new Choose(this);
	}

	@Override
	public String getName() {
		return "choose-" + min;
	}

	public void setK(int k) {
		super.setMin(k);
		super.setMax(k);
	}

	public int getK() {
		return super.getMin();
	}

}
