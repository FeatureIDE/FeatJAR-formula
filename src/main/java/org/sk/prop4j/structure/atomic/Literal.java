package org.sk.prop4j.structure.atomic;

import java.util.Objects;

import org.sk.prop4j.structure.Terminal;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public class Literal extends Terminal implements Atomic {

	protected boolean positive;

	protected Object name;

	public Literal(String name) {
		this(name, true);
	}

	private Literal(String name, boolean positive) {
		this((Object) name, positive);
	}

	protected Literal(Object name, boolean positive) {
		Objects.requireNonNull(name);
		this.name = name;
		this.positive = positive;
	}

	@Override
	public String getName() {
		return name.toString();
	}

	public Object getNameObject() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public Literal flip() {
		positive = !positive;
		return this;
	}

	@Override
	public Literal clone() {
		return new Literal(name, positive);
	}

	@Override
	public int hashCode() {
		int hashCode = positive ? 31 : 37;
		hashCode = (37 * hashCode) + super.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		return positive == ((Literal) other).positive;
	}

	@Override
	public String toString() {
		return (positive ? "+" : "-") + getName();
	}

}
