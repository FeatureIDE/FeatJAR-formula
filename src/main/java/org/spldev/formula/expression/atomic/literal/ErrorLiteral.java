package org.spldev.formula.expression.atomic.literal;

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
		final ErrorLiteral errorLiteral = new ErrorLiteral(name, positive);
		return errorLiteral;
	}

	@Override
	public void setName(String name) {
	}

}
