package org.spldev.formula.structure.atomic.predicate;

import java.util.*;

import org.spldev.formula.structure.term.*;

/**
 *
 * @author Sebastian Krieter
 */
public class NotEquals<D extends Comparable<D>> extends Predicate<D> {

	public NotEquals(Term<D> leftArgument, Term<D> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected NotEquals() {
		super();
	}

	@Override
	public String getName() {
		return "!=";
	}

	@Override
	public Optional<Boolean> eval(List<D> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return Optional.of((values.size() == 2) && (values.get(0).compareTo(values.get(1)) != 0));
	}

	@Override
	public NotEquals<D> cloneNode() {
		return new NotEquals<>();
	}

	@Override
	public Equals<D> flip() {
		final List<? extends Term<D>> children = getChildren();
		return new Equals<>(children.get(0), children.get(1));
	}

}
