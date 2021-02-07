package org.spldev.formula.expression.term;

import java.util.*;

import org.spldev.formula.expression.*;

public class Constant<T> extends Terminal implements Term<T> {

	protected String name;
	protected T value;

	private boolean hasHashCode;
	private int hashCode;

	public Constant(String name, T value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public List<Term<T>> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Constant<T> cloneNode() {
		final Constant<T> constant = new Constant<>(name, value);
		constant.hasHashCode = hasHashCode;
		constant.hashCode = hashCode;
		return constant;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			hashCode = Objects.hash(name, value);
			hasHashCode = true;
		}
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final Constant<?> otherConstant = (Constant<?>) other;
		return (Objects.equals(name, otherConstant.name) &&
			Objects.equals(value, otherConstant.value));
	}

}
