package org.spldev.formula.structure.atomic.literal;

import java.util.*;

import org.spldev.formula.structure.atomic.*;

/**
 * A special {@link Atomic} that is always {@code true}.
 *
 * @author Sebastian Krieter
 */
public final class True extends Literal {

	private static final True INSTANCE = new True();

	private True() {
		super();
	}

	public static True getInstance() {
		return INSTANCE;
	}

	@Override
	public False flip() {
		return Literal.False;
	}

	@Override
	public True cloneNode() {
		return this;
	}

	@Override
	public String getName() {
		return "true";
	}

	@Override
	public int hashCode() {
		return 27 * Objects.hashCode(getName());
	}

	@Override
	public boolean equals(Object other) {
		return other == INSTANCE;
	}

	@Override
	public boolean equalsNode(Object other) {
		return other == INSTANCE;
	}

	@Override
	public String toString() {
		return "true";
	}

}
