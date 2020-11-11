package org.spldev.formulas.structure;

import java.util.*;

public interface Term<D> extends Expression {

	@Override
	List<? extends Term<?>> getChildren();

}
