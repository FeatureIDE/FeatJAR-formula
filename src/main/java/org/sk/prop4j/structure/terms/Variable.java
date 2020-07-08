package org.sk.prop4j.structure.terms;

import org.sk.prop4j.structure.*;

public class Variable<T> extends Terminal implements Term<T> {

	protected String name;
	protected final Class<T> type;
	protected T defaultValue;

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
	public Variable<T> clone() {
		return new Variable<>(name, type);
	}

}
