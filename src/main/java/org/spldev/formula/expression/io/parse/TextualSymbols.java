package org.spldev.formula.expression.io.parse;

/**
 * Symbols for a long textual representation. These are best used for editing by
 * the user due to simplicity and ease of handling.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class TextualSymbols extends Symbols {

	public static final Symbols INSTANCE = new TextualSymbols();

	private TextualSymbols() {
		super();
		setSymbol(Operator.NOT, "not");
		setSymbol(Operator.AND, "and");
		setSymbol(Operator.OR, "or");
		setSymbol(Operator.IMPLIES, "implies");
		setSymbol(Operator.EQUALS, "iff");
	}

}
