package org.spldev.formula.expression.atomic.predicate;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.term.*;
import org.spldev.util.tree.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class LessThanEquals<D extends Comparable<D>> extends Predicate<D> {

	public LessThanEquals(Term<D> leftArgument, Term<D> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected LessThanEquals() {
		super();
	}

	@Override
	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "<=";
	}

	@Override
	public Optional<Boolean> eval(List<D> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return Optional.of((values.size() == 2) && (values.get(0).compareTo(values.get(1)) <= 0));
	}

	@Override
	public Tree<Expression> cloneNode() {
		return new LessThanEquals<>();
	}

	@Override
	public GreaterThan<D> flip() {
		final List<? extends Term<D>> children = getChildren();
		return new GreaterThan<>(children.get(0), children.get(1));
	}

}
