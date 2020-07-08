package org.sk.prop4j.structure.terms;

import org.sk.prop4j.structure.*;

public class Constant<T> extends Terminal implements Term<T> {

	protected String name;
	protected T value;

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
	public Constant<T> clone() {
		return new Constant<>(name, value);
	}

}
