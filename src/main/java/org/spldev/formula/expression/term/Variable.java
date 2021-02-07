package org.spldev.formula.expression.term;

import java.util.*;

import org.spldev.formula.expression.*;

public class Variable<T> extends Terminal implements Term<T> {

	protected String name;
	protected final Class<T> type;
	protected T defaultValue;

	private boolean hasHashCode;
	private int hashCode;

	public Variable(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<T> getType() {
		return type;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public List<Term<T>> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Variable<T> cloneNode() {
		final Variable<T> variable = new Variable<>(name, type);
		variable.defaultValue = defaultValue;
		variable.hasHashCode = hasHashCode;
		variable.hashCode = hashCode;
		return variable;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			hashCode = Objects.hash(name, type);
			hasHashCode = true;
		}
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final Variable<?> otherVariable = (Variable<?>) other;
		return (Objects.equals(name, otherVariable.name) &&
			Objects.equals(type, otherVariable.type));
	}

}
