package org.sk.prop4j.structure.atomic;

/**
 * A special {@link Literal} that is always {@code true}.
 *
 * @author Sebastian Krieter
 */
public class True extends Literal {

	public True() {
		this(true);
	}

	public True(boolean positive) {
		super(Boolean.TRUE, positive);
	}

	@Override
	public True clone() {
		return new True(positive);
	}

	@Override
	public void setName(String name) {
	}

}
