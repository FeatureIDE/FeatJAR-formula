package org.spldev.formula.expression.atomic.literal;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.term.*;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public abstract class Literal extends Terminal implements Atomic {

	public static final True True = org.spldev.formula.expression.atomic.literal.True.getInstance();

	public static final False False = org.spldev.formula.expression.atomic.literal.False.getInstance();

	@Override
	public abstract Literal flip();

	@Override
	public abstract Literal cloneNode();

	@Override
	public List<? extends Term<?>> getChildren() {
		return Collections.emptyList();
	}

	public boolean isPositive() {
		return true;
	}

}
