package org.spldev.formula.structure.term;

import java.util.*;

import org.spldev.formula.structure.*;

public interface Term<D> extends Expression {

	@Override
	List<? extends Term<?>> getChildren();

}
