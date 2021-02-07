package org.spldev.formula.expression;

import java.util.*;

public interface Formula extends Expression {

	@Override
	List<? extends Expression> getChildren();

}
