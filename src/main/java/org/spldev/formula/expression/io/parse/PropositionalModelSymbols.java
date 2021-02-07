package org.spldev.formula.expression.io.parse;

/**
 * Symbols for a representation like in Java. These are inherently incomplete
 * and should only be used if absolutely necessary.
 * 
 * @author Sebastian Krieter
 */
public class PropositionalModelSymbols extends Symbols {

	public static final Symbols INSTANCE = new PropositionalModelSymbols();

	private PropositionalModelSymbols() {
		super();
		setSymbol(Operator.NOT, "!");
		setSymbol(Operator.AND, "&");
		setSymbol(Operator.OR, "|");
		setSymbol(Operator.EQUALS, "==");
		setSymbol(Operator.IMPLIES, "=>");
		setTextual(false);
	}

}
