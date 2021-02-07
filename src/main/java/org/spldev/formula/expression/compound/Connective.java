package org.spldev.formula.expression.compound;

import java.util.*;

import org.spldev.formula.expression.*;

/**
 * A logical connector that connects other {@link Formula}.
 *
 * @author Sebastian Krieter
 */
public interface Connective extends Formula {

	@Override
	List<Formula> getChildren();
}
