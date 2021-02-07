package org.spldev.formula.expression.io.parse;

/**
 * Symbols for a short textual representation. Best used for serialization since
 * they fall in the ASCII range but are still relatively short.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class ShortSymbols extends Symbols {

	public static final Symbols INSTANCE = new ShortSymbols();

	private ShortSymbols() {
		super();
		setSymbol(Operator.NOT, "-");
		setSymbol(Operator.AND, "&");
		setSymbol(Operator.OR, "|");
		setSymbol(Operator.IMPLIES, "=>");
		setSymbol(Operator.EQUALS, "<=>");
		setTextual(false);
	}

}
