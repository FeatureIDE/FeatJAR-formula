package org.sk.prop4j.structure.atomic;

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
	public False clone() {
		return new False(positive);
	}

	@Override
	public void setName(String name) {
	}

}
