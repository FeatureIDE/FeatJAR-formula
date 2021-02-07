package org.spldev.formula.expression.compound;

import java.util.*;

import org.spldev.formula.expression.*;

/**
 * A logical connector that is {@code true} iff all of its children are
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public abstract class Compound extends NonTerminal implements Formula {

	public Compound(Collection<? extends Formula> nodes) {
		super(nodes);
	}

	public Compound(Formula... nodes) {
		super(nodes);
	}

	protected Compound() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Formula> getChildren() {
		return (List<Formula>) super.getChildren();
	}

}
