package org.spldev.formula.expression.atomic;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.term.*;

/**
 * An atomic formula.
 *
 * @author Sebastian Krieter
 */
public interface Atomic extends Formula {

	@Override
	List<? extends Term<?>> getChildren();

	Atomic flip();

}
