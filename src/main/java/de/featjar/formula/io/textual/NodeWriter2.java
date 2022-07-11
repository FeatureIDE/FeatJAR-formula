/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
 * 
 * This file is part of formula.
 * 
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 * 
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.formula.io.textual;

import java.util.*;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Terminal;
import de.featjar.formula.io.textual.Symbols.*;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.compound.*;
import de.featjar.util.tree.visitor.DfsVisitor;
import de.featjar.util.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.*;
import de.featjar.formula.structure.atomic.literal.*;
import de.featjar.formula.structure.compound.*;
import de.featjar.util.tree.visitor.*;

/**
 * Converts a propositional node to a String object.
 *
 * @author Thomas Thüm
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public class NodeWriter2 implements DfsVisitor<Void, Formula> {

	private final StringBuilder sb = new StringBuilder();

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Notation notation = getNotation();
		switch (notation) {
		case INFIX:
			// if literal or not infixable or != 2 children
			final Formula currentNode = TreeVisitor.getCurrentNode(path);

			alignLine(path.size());
			if (currentNode instanceof Terminal) {
				if (currentNode instanceof Literal) {
					if (!((Literal) currentNode).isPositive()) {
						sb.append(getSymbols().getSymbol(Operator.NOT));
						sb.append(getSymbols().isTextual() ? " " : "");
					}
				}
				sb.append(variableToString(currentNode.getName()));
			} else if (currentNode.getChildren().size() != 2) {

			} else if (currentNode instanceof Formula) {
				Symbols.getOperator((Formula) currentNode);
			}
			break;
		case PREFIX:
			// print
			break;
		case POSTFIX:
			// nothing
			break;
		default:
			throw new IllegalStateException("Unknown notation: " + notation);
		}
		return VisitorResult.Continue;
	}

	@Override
	public VisitorResult visit(List<Formula> path) {
		final Notation notation = getNotation();
		switch (notation) {
		case INFIX:
			// if not literal and infixable and == 2 children
			final Formula currentNode = TreeVisitor.getCurrentNode(path);
			if (currentNode.getChildren().size() == 2) {
				alignLine(path.size());
				Symbols.getOperator((Formula) currentNode);
			}
			break;
		case PREFIX:
			// nothing
			break;
		case POSTFIX:
			// nothing
			break;
		default:
			throw new IllegalStateException("Unknown notation: " + notation);
		}
		return VisitorResult.Continue;
	}

	@Override
	public VisitorResult lastVisit(List<Formula> path) {
		final Notation notation = getNotation();
		switch (notation) {
		case INFIX:
			// nothing
			break;
		case PREFIX:
			// nothing
			break;
		case POSTFIX:
			// print
			break;
		default:
			throw new IllegalStateException("Unknown notation: " + notation);
		}
		return VisitorResult.Continue;
	}

	/**
	 * The type of notation of the formula.
	 *
	 * @author Timo Günther
	 */
	public enum Notation {
		/**
		 * <p>
		 * The infix notation. Operators are written between operands where possible.
		 *
		 * <p>
		 * Examples:
		 * <ul>
		 * <li><em>A &amp; B &amp; C</em></li>
		 * <li><em>A =&gt; B &lt;=&gt; -A | B</em></li>
		 * <li><em>atleast2(A, B, C, D) &amp; atmost3(A, B, C, D)</em></li>
		 * </ul>
		 */
		INFIX,
		/**
		 * <p>
		 * The prefix notation. Operators are written before the operands.
		 *
		 * <p>
		 * Examples:
		 * <ul>
		 * <li><em>(&amp; A B C)</em></li>
		 * <li><em>(&lt;=&gt; (=&gt; A B) (| (- A) B)</em></li>
		 * <li><em>(&amp; (atleast2 A B C D) (atmost3 A B C D))</em></li>
		 * </ul>
		 */
		PREFIX,
		/**
		 * <p>
		 * The postfix notation. Operators are written after the operands.
		 *
		 * <p>
		 * Examples:
		 * <ul>
		 * <li><em>(A B C &amp;)</em></li>
		 * <li><em>((A B =&gt;) ((A -) B |) &lt;=&gt;)</em></li>
		 * <li><em>((A B C D atleast2) (A B C D atmost3) &gt;)</em></li>
		 * </ul>
		 */
		POSTFIX,
	}

	/**
	 * The line format used.
	 *
	 * @author Timo Günther
	 */
	public enum LineFormat {
		/**
		 * <p>
		 * Return a single line without any line breaks.
		 * </p>
		 */
		SINGLE,
		/**
		 * <p>
		 * Return multiple lines with indentation corresponding to nesting depth of the
		 * nodes.
		 * </p>
		 */
		TREE,
	}

	/**
	 * The symbols for the operations.
	 * 
	 * @see #setSymbols(Symbols)
	 */
	private Symbols symbols = ShortSymbols.INSTANCE;
	/** The notation to use. */
	private Notation notation = Notation.INFIX;
	/** The line format to use. */
	private LineFormat lineFormat = LineFormat.SINGLE;
	/**
	 * If true, this writer will always place brackets, even if they are
	 * semantically irrelevant.
	 */
	private boolean enforceBrackets = false;
	/** If true, this writer will enquote variables if they contain whitespace. */
	private boolean enquoteWhitespace = false;

	private String separator = ",";
	private String tab = "\t";
	private String newLine = System.lineSeparator();

	/**
	 * Sets the symbols to use for the operations. These are:
	 * <ul>
	 * <li>{@link Not}</li>
	 * <li>{@link And}</li>
	 * <li>{@link Or}</li>
	 * <li>{@link Implies}</li>
	 * <li>{@link Biimplies}</li>
	 * <li>{@link Choose}</li>
	 * <li>{@link AtLeast}</li>
	 * <li>{@link AtMost}</li>
	 * <li>{@link Between}</li>
	 * <li>{@link ForAll}</li>
	 * <li>{@link Exists}</li>
	 * </ul>
	 * By default, the set of short symbols is used.
	 *
	 * @param symbols symbols for the operations; not null
	 * @see LogicalSymbols
	 * @see TextualSymbols
	 * @see ShortSymbols
	 * @see JavaSymbols
	 */
	public void setSymbols(Symbols symbols) {
		this.symbols = symbols;
	}

	/**
	 * Returns the symbols to use for the operations.
	 *
	 * @return the symbols to use for the operations
	 */
	protected Symbols getSymbols() {
		return symbols;
	}

	/**
	 * Sets the notation to use. By default, this is the {@link Notation#INFIX
	 * infix} notation.
	 *
	 * @param notation notation to use
	 */
	public void setNotation(Notation notation) {
		this.notation = notation;
	}

	/**
	 * Returns the notation to use.
	 *
	 * @return the notation to use
	 */
	protected Notation getNotation() {
		return notation;
	}

	public LineFormat getLineFormat() {
		return lineFormat;
	}

	public void setLineFormat(LineFormat lineFormat) {
		this.lineFormat = lineFormat;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getNewLine() {
		return newLine;
	}

	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}

	/**
	 * Sets the enforcing brackets flag. If {@code true}, this writer will always
	 * place brackets, even if they are semantically irrelevant.
	 *
	 * @param enforceBrackets if {@code true} the writer will use parentheses for
	 *                        every sub-expression.
	 */
	public void setEnforceBrackets(boolean enforceBrackets) {
		this.enforceBrackets = enforceBrackets;
	}

	/**
	 * Returns the enforcing brackets flag.
	 *
	 * @return the enforcing brackets flag
	 */
	protected boolean isEnforceBrackets() {
		return enforceBrackets;
	}

	/**
	 * Sets the enquoting whitespace flag. If {@code true}, this writer will enquote
	 * variables if they contain whitespace.
	 *
	 * @param enquoteWhitespace if {@code true} the writer will enquote all variable
	 *                          names with white spaces in them.
	 */
	public void setEnquoteWhitespace(boolean enquoteWhitespace) {
		this.enquoteWhitespace = enquoteWhitespace;
	}

	/**
	 * Returns the enquoting whitespace flag.
	 *
	 * @return the enquoting whitespace flag
	 */
	protected boolean isEnquoteWhitespace() {
		return enquoteWhitespace;
	}

	/**
	 * Converts a variable into the specified textual representation.
	 *
	 * @param variable a variable to convert; not null
	 * @return the textual representation; not null
	 */
	private String variableToString(String variable) {
		return (isEnquoteWhitespace() && (containsWhitespace(variable) || equalsSymbol(variable)))
			? '"' + variable + '"'
			: variable;
	}

	private void alignLine(int depth) {
		switch (lineFormat) {
		case SINGLE:
			break;
		case TREE:
			if (depth > 0) {
				sb.append('\n');
				for (int i = 0; i < depth; i++) {
					sb.append('\t');
				}
			}
			break;
		default:
			throw new IllegalStateException("Unknown line format: " + lineFormat);
		}
	}

	/**
	 * Returns true iff the given string equals one of the symbols.
	 *
	 * @param s string potentially equaling a symbol; not null
	 * @return whether the string equals one of the symbols
	 */
	private boolean equalsSymbol(String s) {
		return getSymbols().parseSymbol(s) != Operator.UNKNOWN;
	}

	/**
	 * Returns true iff the given string contains a whitespace character.
	 *
	 * @param s string potentially containing whitespace; not null
	 * @return whether the string contains whitespace
	 */
	private static boolean containsWhitespace(String s) {
		return s.matches(".*?\\s+.*");
	}

}
