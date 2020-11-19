package org.spldev.formula.structure.atomic.literal;

/**
 * A special {@link Literal} that holds an unparsable sub expression from a
 * formula.
 *
 * @author Sebastian Krieter
 */
public class ErrorLiteral extends LiteralVariable {

	public ErrorLiteral(String error) {
		super(error, true);
	}

	public ErrorLiteral(String error, boolean positive) {
		super(error, positive);
	}

	@Override
	public ErrorLiteral cloneNode() {
		return new ErrorLiteral(name, positive);
	}

	@Override
	public void setName(String name) {
	}

}
