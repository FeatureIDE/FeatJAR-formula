package org.spldev.formula.structure;

import java.util.*;

public interface Formula extends Expression {

	@Override
	List<? extends Expression> getChildren();

}
