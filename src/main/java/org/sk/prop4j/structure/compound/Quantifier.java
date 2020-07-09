package org.sk.prop4j.structure.compound;

import java.util.*;

import org.sk.prop4j.structure.*;

public abstract class Quantifier extends NonTerminal<Formula> {

	protected Variable<?> boundVariable;

	public Quantifier(Variable<?> boundVariable, Formula formula) {
		super(formula);
		setBoundVariable(boundVariable);
	}

	public Quantifier(Quantifier oldNode) {
		super(oldNode);
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
	public Quantifier clone() {
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
