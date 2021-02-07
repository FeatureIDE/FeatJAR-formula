package org.spldev.formula.expression;

import org.spldev.util.*;
import org.spldev.util.data.*;

/**
 * Abstract creator to derive an element from a {@link Cache}.
 *
 * @author Sebastian Krieter
 */
@FunctionalInterface
public interface ExpressionProvider extends Provider<Formula> {

	Identifier<Formula> identifier = new Identifier<>();

	@Override
	default Identifier<Formula> getIdentifier() {
		return identifier;
	}

	static ExpressionProvider empty() {
		return (f, m) -> Result.empty();
	}

	static ExpressionProvider of(Formula formula) {
		return (f, m) -> Result.of(formula);
	}

}
