package org.spldev.formulas.parse;

/**
 * Symbols for a logical representation. These are best used for displaying to
 * the user due to brevity and beauty. Since they consist of unwieldy Unicode
 * characters, do not use them for editing or serialization.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class LogicalSymbols extends Symbols {

	public static final Symbols INSTANCE = new LogicalSymbols();

	private LogicalSymbols() {
		super();
		setSymbol(Operator.NOT, "\u00AC");
		setSymbol(Operator.AND, "\u2227");
		setSymbol(Operator.OR, "\u2228");
		setSymbol(Operator.IMPLIES, "\u21D2");
		setSymbol(Operator.EQUALS, "\u21D4");
		setTextual(false);
	}

}
