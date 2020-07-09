package org.sk.prop4j.structure;

public interface Term<T> extends Expression {

	@Override
	Term<T> clone();

}
