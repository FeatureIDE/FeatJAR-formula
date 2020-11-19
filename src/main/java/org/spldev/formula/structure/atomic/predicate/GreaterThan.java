package org.spldev.formula.structure.atomic.predicate;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.term.*;
import org.spldev.tree.structure.*;

/**
 *
 * @author Sebastian Krieter
 */
public class GreaterThan<D extends Comparable<D>> extends Predicate<D> {

	public GreaterThan(Term<D> leftArgument, Term<D> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected GreaterThan() {
		super();
	}

	@Override
	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return ">";
	}

	@Override
	public Optional<Boolean> eval(List<D> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return Optional.of((values.size() == 2) && (values.get(0).compareTo(values.get(1)) > 0));
	}

	@Override
	public Tree<Expression> cloneNode() {
		return new GreaterThan<>();
	}

	@Override
	public LessThanEquals<D> flip() {
		final List<? extends Term<D>> children = getChildren();
		return new LessThanEquals<>(children.get(0), children.get(1));
	}

}
