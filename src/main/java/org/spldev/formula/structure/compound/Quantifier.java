package org.spldev.formula.structure.compound;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.term.*;

public abstract class Quantifier extends Compound implements Connective {

	protected Variable<?> boundVariable;

	public Quantifier(Variable<?> boundVariable, Formula formula) {
		super(formula);
		setBoundVariable(boundVariable);
	}

	protected Quantifier(Quantifier oldNode) {
		super();
		setBoundVariable(boundVariable);
	}

	public Variable<?> getBoundVariable() {
		return boundVariable;
	}

	public void setBoundVariable(Variable<?> boundVariable) {
		Objects.requireNonNull(boundVariable);
		this.boundVariable = boundVariable;
	}

	public void setFormula(Formula formula) {
		Objects.requireNonNull(formula);
		setChildren(Arrays.asList(formula));
	}

	@Override
	public Quantifier cloneNode() {
		throw new IllegalStateException();
	}

	@Override
	public int computeHashCode() {
		int hashCode = Objects.hashCode(boundVariable);
		hashCode = (37 * hashCode) + super.computeHashCode();
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (!super.equalsNode(other)) {
			return false;
		}
		return Objects.equals(boundVariable, ((Quantifier) other).boundVariable);
	}

}
