package org.spldev.formulas.structure;

import java.util.*;

public interface Formula extends Expression {

//	@Override
//	Formula clone();
	@Override
	List<? extends Expression> getChildren();

}
