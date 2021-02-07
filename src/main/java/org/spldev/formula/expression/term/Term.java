package org.spldev.formula.expression.term;

import java.util.*;

import org.spldev.formula.expression.*;

public interface Term<D> extends Expression {

	@Override
	List<? extends Term<?>> getChildren();

}
