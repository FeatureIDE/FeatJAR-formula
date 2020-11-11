package org.spldev.formulas.parse;

/**
 * Symbols for a representation like in Java. These are inherently incomplete
 * and should only be used if absolutely necessary.
 * 
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class JavaSymbols extends Symbols {

	public static final Symbols INSTANCE = new JavaSymbols();

	private JavaSymbols() {
		super();
		setSymbol(Operator.NOT, "!");
		setSymbol(Operator.AND, "&&");
		setSymbol(Operator.OR, "||");
		setSymbol(Operator.EQUALS, "==");
		setTextual(false);
	}

}
