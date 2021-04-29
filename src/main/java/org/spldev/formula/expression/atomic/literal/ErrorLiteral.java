package org.spldev.formula.expression.atomic.literal;

/**
 * A special {@link Literal} that holds an unparsable sub expression from a
 * formula.
 *
 * @author Sebastian Krieter
 */
public class ErrorLiteral extends Literal {

	private String error;
	private boolean positive;
	
	public ErrorLiteral(String error) {
		this(error, true);
	}

	public ErrorLiteral(String error, boolean positive) {
		this.error = error;
		this.positive = positive;
	}

	@Override
	public ErrorLiteral cloneNode() {
		return new ErrorLiteral(error, positive);
	}

	@Override
	public String getName() {
		return "Error: " + error;
	}

	@Override
	public Literal flip() {
		return new ErrorLiteral(error, !positive);
	}

	public boolean isPositive() {
		return positive;
	}

}
