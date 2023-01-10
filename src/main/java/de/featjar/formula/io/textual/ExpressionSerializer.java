/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.io.textual;

import de.featjar.formula.io.textual.Symbols.Operator;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.AtLeast;
import de.featjar.formula.structure.formula.connective.AtMost;
import de.featjar.formula.structure.formula.connective.Between;
import de.featjar.formula.structure.formula.connective.BiImplies;
import de.featjar.formula.structure.formula.connective.Choose;
import de.featjar.formula.structure.formula.connective.Exists;
import de.featjar.formula.structure.formula.connective.ForAll;
import de.featjar.formula.structure.formula.connective.IConnective;
import de.featjar.formula.structure.formula.connective.Implies;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import java.util.List;

/**
 * Serializes expressions.
 * Currently only supports a subset of expressions.
 * TODO: write new serializer for all expression types
 *
 * @author Thomas Thüm
 * @author Timo Günther
 * @author Sebastian Krieter
 * @deprecated does not work reliably at the moment
 */
@Deprecated
public class ExpressionSerializer {

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
     * <li>{@link BiImplies}</li>
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
     * Converts the given node into the specified textual representation.
     *
     * @param expression the formula to write
     * @return the textual representation; not null
     */
    public String serialize(IExpression expression) {
        final StringBuilder sb = new StringBuilder();
        nodeToString(expression, null, sb, -1);
        return sb.toString();
    }

    public void serialize(IExpression expression, StringBuilder sb) {
        nodeToString(expression, null, sb, -1);
    }

    private void nodeToString(IExpression expression, Operator parent, StringBuilder sb, int depth) {
        if (expression == null) {
            sb.append((String) null);
        } else {
            if (expression instanceof Not) {
                final IExpression child = expression.getFirstChild().orElse(null);
                if (child instanceof Literal) {
                    literalToString(((Literal) child.cloneTree()).invert(), sb, depth + 1);
                    return;
                }
            }
            if (expression instanceof Literal) {
                literalToString((Literal) expression, sb, depth + 1);
            } else {
                operationToString((IConnective) expression, parent, sb, depth + 1);
            }
        }
    }

    /**
     * Converts a literal into the specified textual representation.
     *
     * @param l      a literal to convert; not null
     * @param sb     the {@link StringBuilder} containing the textual
     *               representation.
     */
    private void literalToString(Literal l, StringBuilder sb, int depth) {
        alignLine(sb, depth);
        final String s = variableToString(l.getExpression().getName());
        if (!l.isPositive()) {
            final Notation notation = getNotation();
            switch (notation) {
                case INFIX:
                    sb.append(getSymbols().getSymbol(Operator.NOT));
                    sb.append(getSymbols().isTextual() ? " " : "");
                    sb.append(s);
                    break;
                case PREFIX:
                    sb.append('(');
                    sb.append(getSymbols().getSymbol(Operator.NOT));
                    sb.append(' ');
                    sb.append(s);
                    sb.append(')');
                    break;
                case POSTFIX:
                    sb.append('(');
                    sb.append(s);
                    sb.append(' ');
                    sb.append(getSymbols().getSymbol(Operator.NOT));
                    sb.append(')');
                    break;
                default:
                    throw new IllegalStateException("Unknown notation: " + notation);
            }
        } else {
            sb.append(s);
        }
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

    /**
     * Converts an operation (i.e. a node that is not a literal) into the specified
     * textual representation.
     *
     * @param node   an operation to convert; not null
     * @param parent the class of the node's parent; null if not available (i.e. the
     *               current node is the root node)
     * @param sb     the {@link StringBuilder} containing the textual
     *               representation.
     */
    private void operationToString(IConnective node, Operator parent, StringBuilder sb, int depth) {
        alignLine(sb, depth);
        final List<? extends IExpression> children = node.getChildren();
        if (children.size() == 0) {
            sb.append("()");
            return;
        }

        final Operator operator = Symbols.getOperator(node);
        final Notation notation = getNotation();
        switch (notation) {
            case INFIX:
                if (isInfixCompatibleOperation(node)) {
                    final int orderParent;
                    final int orderChild;
                    final boolean parenthesis = (isEnforceBrackets()
                            || ((orderParent = getSymbols().getOrder(parent))
                                    > (orderChild = getSymbols().getOrder(operator)))
                            || ((orderParent == orderChild)
                                    && (orderParent == getSymbols().getOrder(Operator.IMPLIES))));
                    if (parenthesis) {
                        sb.append('(');
                    }
                    nodeToString(children.get(0), operator, sb, depth);
                    for (int i = 1; i < children.size(); i++) {
                        sb.append(' ');
                        sb.append(getSymbols().getSymbol(operator));
                        sb.append(' ');
                        nodeToString(children.get(i), operator, sb, depth);
                    }
                    if (parenthesis) {
                        sb.append(')');
                    }
                } else {
                    sb.append(getSymbols().getSymbol(operator));
                    if ((node instanceof Not) && (getSymbols().isTextual())) {
                        sb.append(' ');
                    }
                    sb.append('(');
                    nodeToString(children.get(0), operator, sb, depth);
                    for (int i = 1; i < children.size(); i++) {
                        sb.append(getSeparator());
                        nodeToString(children.get(i), operator, sb, depth);
                    }
                    sb.append(')');
                }
                break;
            case PREFIX:
                sb.append('(');
                sb.append(getSymbols().getSymbol(operator));
                sb.append(' ');
                nodeToString(children.get(0), operator, sb, depth);
                for (int i = 1; i < children.size(); i++) {
                    sb.append(' ');
                    nodeToString(children.get(i), operator, sb, depth);
                }
                sb.append(')');

                break;
            case POSTFIX:
                sb.append('(');
                nodeToString(children.get(0), operator, sb, depth);
                for (int i = 1; i < children.size(); i++) {
                    sb.append(' ');
                    nodeToString(children.get(i), operator, sb, depth);
                }
                sb.append(' ');
                sb.append(getSymbols().getSymbol(operator));
                sb.append(')');
                break;
            default:
                throw new IllegalStateException("Unknown notation: " + notation);
        }
    }

    private void alignLine(StringBuilder sb, int depth) {
        switch (lineFormat) {
            case SINGLE:
                break;
            case TREE:
                if (depth > 0) {
                    sb.append('\n');
                    sb.append("\t".repeat(depth));
                }
                break;
            default:
                throw new IllegalStateException("Unknown line format: " + lineFormat);
        }
    }

    /**
     * Returns true iff the given operation can be written in infix notation. For
     * example, this is true for operations such as {@link And}, which can be
     * written as <em>A and B</em> instead of <em>and(A, B)</em>. By contrast, this
     * is false for unary operations (i.e. {@link Not}). This is also false for
     * {@link Choose}, {@link AtLeast} and {@link AtMost}.
     *
     * @param expression operation in question
     * @return true iff the given operation can be written in infix notation
     */
    private boolean isInfixCompatibleOperation(IExpression expression) {
        return (expression instanceof And)
                || (expression instanceof Or)
                || (expression instanceof Implies)
                || (expression instanceof BiImplies);
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
