package org.spldev.formulas.structure.atomic;

/**
 * A special {@link Literal} that is always {@code false}.
 *
 * @author Sebastian Krieter
 */
public class False extends Literal {

	public False() {
		this(true);
	}

	public False(boolean positive) {
		super(Boolean.FALSE, positive);
	}

	@Override
	public False cloneNode() {
		return new False(positive);
	}

	@Override
	public void setName(String name) {
	}

}
