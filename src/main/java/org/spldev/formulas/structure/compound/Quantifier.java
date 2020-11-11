package org.spldev.formulas.structure.compound;

import java.util.*;

import org.spldev.formulas.structure.*;
import org.spldev.formulas.structure.terms.*;

public abstract class Quantifier extends Compound {

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
	public int hashCode() {
		int hashCode = Objects.hashCode(boundVariable);
		hashCode = (37 * hashCode) + super.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		return Objects.equals(boundVariable, ((Quantifier) other).boundVariable);
	}

}
