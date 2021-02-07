package org.spldev.formula.expression.atomic.predicate;

import java.util.*;

import org.spldev.formula.expression.term.*;

/**
 *
 * @author Sebastian Krieter
 */
public class Equals<D extends Comparable<D>> extends Predicate<D> {

	public Equals(Term<D> leftArgument, Term<D> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected Equals() {
		super();
	}

	@Override
	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "=";
	}

	@Override
	public Optional<Boolean> eval(List<D> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return Optional.of((values.size() == 2) && (values.get(0).compareTo(values.get(1)) == 0));
	}

	@Override
	public Equals<D> cloneNode() {
		return new Equals<>();
	}

	@Override
	public NotEquals<D> flip() {
		final List<? extends Term<D>> children = getChildren();
		return new NotEquals<>(children.get(0), children.get(1));
	}

}
