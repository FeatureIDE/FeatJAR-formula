package org.sk.prop4j.structure.atomic;

import java.util.List;

import org.sk.prop4j.structure.Term;

/**
 *
 * @author Sebastian Krieter
 */
public class EqualReal extends Equal<Double> {

	public EqualReal(Term<Double> leftArgument, Term<Double> rightArgument) {
		super(leftArgument, rightArgument);
	}

	private EqualReal(EqualReal oldNode) {
		super(oldNode);
	}

	@Override
	public EqualReal clone() {
		return new EqualReal(this);
	}

	@Override
	public boolean eval(List<Double> values) {
		return (values.size() == 2) && (values.get(0) == values.get(1));
	}

}
