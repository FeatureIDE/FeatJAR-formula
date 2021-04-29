package org.spldev.formula.expression.io.parse;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.compound.*;

public class Symbols {

	public enum Operator {
		NOT("not", 0), AND("and", 6), OR("or", 5), IMPLIES("implies", 4), EQUALS("equals", 3), CHOOSE("choose", 2),
		ATLEAST("atleast", 2), BETWEEN("between", 2), ATMOST("atmost", 2), EXISTS("exists", 1), FORALL("forall", 1),
		UNKOWN("?", -1);

		private String defaultName;
		private int priority;

		Operator(String defaultName, int priority) {
			this.defaultName = defaultName;
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}
	}

	private final Map<String, Operator> symbolToOperator = new HashMap<>();
	private final Map<Operator, String> operatorToSymbol = new HashMap<>();

	private boolean textual = true;

	public Symbols() {
		this(true);
	}

	public Symbols(boolean addTextualSymbols) {
		if (addTextualSymbols) {
			for (final Operator operator : Operator.values()) {
				setSymbol(operator, operator.defaultName);
			}
		}
	}

	public final void setSymbol(Operator operator, String name) {
		symbolToOperator.put(name, operator);
		operatorToSymbol.put(operator, name);
	}

	public Operator parseSymbol(String symbol) {
		final Operator operator = symbolToOperator.get(symbol);
		return operator != null ? operator : Operator.UNKOWN;
	}

	public String getSymbol(Operator operator) {
		final String symbol = operatorToSymbol.get(operator);
		return symbol != null ? symbol : operator.defaultName;
	}

	protected Operator getOperator(Formula node) throws IllegalArgumentException {
		if (node instanceof Connective) {
			if (node instanceof Not) {
				return Operator.NOT;
			}
			if (node instanceof And) {
				return Operator.AND;
			}
			if (node instanceof Or) {
				return Operator.OR;
			}
			if (node instanceof Implies) {
				return Operator.IMPLIES;
			}
			if (node instanceof Biimplies) {
				return Operator.EQUALS;
			}
			if (node instanceof AtLeast) {
				return Operator.ATLEAST;
			}
			if (node instanceof AtMost) {
				return Operator.ATMOST;
			}
			if (node instanceof Choose) {
				return Operator.CHOOSE;
			}
			if (node instanceof Between) {
				return Operator.BETWEEN;
			}
			if (node instanceof ForAll) {
				return Operator.FORALL;
			}
			if (node instanceof Exists) {
				return Operator.EXISTS;
			}
			return Operator.UNKOWN;
		}
		throw new IllegalArgumentException("Unrecognized node type: " + node.getClass());
	}

	public boolean isTextual() {
		return textual;
	}

	public void setTextual(boolean textual) {
		this.textual = textual;
	}

	/**
	 * Assigns a number to every operator. For instance, that {@link And} has a
	 * higher order than {@link Or} means that <em>(A and B or C)</em> is equal to
	 * <em>((A and B) or C)</em>.
	 *
	 * @param operator operator type
	 * @return the order assigned to the type of node
	 */
	protected int getOrder(Operator operator) {
		return operator != null ? operator.getPriority() : Operator.UNKOWN.getPriority();
	}

}
