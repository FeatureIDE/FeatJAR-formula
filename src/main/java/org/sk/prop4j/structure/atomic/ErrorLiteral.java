package org.sk.prop4j.structure.atomic;

/**
 * A special {@link Literal} that holds an unparsable sub expression from a
 * formula.
 *
 * @author Sebastian Krieter
 */
public class ErrorLiteral extends Literal {

	public ErrorLiteral(Object error) {
		super(error, true);
	}

	public ErrorLiteral(Object error, boolean positive) {
		super(error, positive);
	}

	@Override
	public ErrorLiteral clone() {
		return new ErrorLiteral(name, positive);
	}

	@Override
	public void setName(String name) {
	}

}
