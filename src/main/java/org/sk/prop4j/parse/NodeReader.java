package org.sk.prop4j.parse;

import java.text.ParseException;
import java.util.*;
import java.util.regex.*;

import org.sk.prop4j.parse.ErrorType.ErrorEnum;
import org.sk.prop4j.parse.Symbols.Operator;
import org.sk.prop4j.structure.Formula;
import org.sk.prop4j.structure.atomic.*;
import org.sk.prop4j.structure.compound.*;
import org.w3c.dom.Node;

/**
 * This class can be used to parse propositional formulas.
 *
 * @author Dariusz Krolikowski
 * @author David Broneske
 * @author Fabian Benduhn
 * @author Thomas Thuem
 * @author Florian Proksch
 * @author Stefan Krueger
 * @author Sebastian Krieter
 */
public class NodeReader {

	private static final char SPACE = ' ';
	private static final char QUOTE = '\"';
	private static final char PARENTHESIS_OPEN = '(';
	private static final char PARENTHESIS_CLOSE = ')';

	public enum ErrorMessage {
		INVALID_FEATURE_NAME("'%s' is no valid feature name."), //
		NULL_CONSTRAINT("Constraint is null."), //
		EMPTY_CONSTRAINT("Contraint is empty."),
		PARENTHESES_IN_FEATURE_NAMES("Parenthesis are not allowed in feature names."),
		INVALID_CLOSING_PARENTHESES("To many closing parentheses."),
		INVALID_NUMBER_OF_QUOTATION_MARKS("Invalid number of quotation marks."),
		INVALID_OPENING_PARENTHESES("There are unclosed opening parentheses."),
		EMPTY_EXPRESSION("Sub expression is empty."), MISSING_NAME("Missing feature name or expression: %s"),
		MISSING_NAME_LEFT("Missing feature name or expression on left side: %s"),
		MISSING_NAME_RIGHT("Missing feature name or expression on right side: %s"),
		MISSING_OPERATOR("Missing operator: %s");

		private String message;

		private ErrorMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	private static final void throwParsingError(ErrorMessage message, int offset, Object... context)
		throws ParseException {
		throw new ParseException(String.format(message.getMessage(), context), offset);
	}

	public enum ErrorHandling {
		ABORT, REMOVE, KEEP
	}

	private static final String featureNameMarker = "#";
	private static final String subExpressionMarker = "$";
	private static final String replacedFeatureNameMarker = featureNameMarker + "_";
	private static final String replacedSubExpressionMarker = subExpressionMarker + "_";

	private static final Pattern subExpressionPattern = Pattern.compile(Pattern.quote(subExpressionMarker) + "\\d+");
	private static final Pattern featureNamePattern = Pattern.compile(Pattern.quote(featureNameMarker) + "\\d+");

	private static final Pattern parenthesisPattern = Pattern.compile("\\(([^()]*)\\)");
	private static final Pattern quotePattern = Pattern.compile("\\\"(.*?)\\\"");

	private static final String symbolPatternString = "\\s*(%s)\\s*";

	private static final Operator[] operators = Operator.values();
	static {
		Collections.sort(Arrays.asList(operators), (o1, o2) -> o2.getPriority() - o1.getPriority());
	}

	private HashSet<String> featureNames;

	private Symbols symbols = ShortSymbols.INSTANCE;

	public ErrorType errorType = new ErrorType(ErrorEnum.None);
	private ParseException errorMessage = null;

	private ErrorHandling ignoreMissingFeatures = ErrorHandling.ABORT;
	private ErrorHandling ignoreUnparsableSubExpressions = ErrorHandling.ABORT;

	public Collection<String> getFeatureNames() {
		return featureNames;
	}

	public Symbols getSymbols() {
		return symbols;
	}

	public void setSymbols(Symbols symbols) {
		this.symbols = symbols;
	}

	public void setFeatureNames(Collection<String> featureNames) {
		this.featureNames = (featureNames == null) ? null : new HashSet<>(featureNames);
	}

	/**
	 * Parses a constraint and create a corresponding {@link Node} tree.
	 *
	 * @param constraint The constraint as a string representation.
	 * @return A node representing the constraint.
	 */
	public Formula stringToNode(String constraint) {
		errorMessage = null;
		if (constraint == null) {
			errorMessage = new ParseException(ErrorMessage.NULL_CONSTRAINT.getMessage(), 0);
			return null;
		}
		try {
			return parseNode(constraint);
		} catch (final ParseException e) {
			errorMessage = e;
			switch (ignoreUnparsableSubExpressions) {
			case KEEP:
				return new ErrorLiteral(constraint);
			case REMOVE:
				return null;
			case ABORT:
			default:
				return null;
			}
		}
	}

	/**
	 * Parses a constraint and create a corresponding {@link Node} tree.
	 *
	 * @param constraint   The constraint to be parsed.
	 * @param featureNames The valid feature names.
	 * @return A node representing the constraint.
	 */
	public Formula stringToNode(String constraint, Collection<String> featureNames) {
		setFeatureNames(featureNames);
		return stringToNode(constraint);
	}

	/**
	 * returns true if constraint is well formed
	 *
	 * @param constraint
	 * @return
	 */
	public boolean isWellFormed(String constraint) {
		setFeatureNames(null);
		return stringToNode(constraint) != null;
	}

	/**
	 * returns true if constraint is well formed
	 *
	 * @param constraint   constraint supposed to be checked
	 * @param featureNames list of feature names
	 * @return true if constraint is well formed
	 */
	public boolean isWellFormed(String constraint, final Collection<String> featureNames) {
		setFeatureNames(featureNames);
		return stringToNode(constraint) != null;
	}

	/**
	 * if stringToNode or isWellFormed were called with not well-formed constraint
	 * this method returns the error message otherwise empty String
	 *
	 * @return
	 */
	public ParseException getErrorMessage() {
		return errorMessage;
	}

	public ErrorHandling ignoresMissingFeatures() {
		return ignoreMissingFeatures;
	}

	public void setIgnoreMissingFeatures(ErrorHandling ignoreMissingFeatures) {
		this.ignoreMissingFeatures = ignoreMissingFeatures;
	}

	public ErrorHandling isIgnoreUnparsableSubExpressions() {
		return ignoreUnparsableSubExpressions;
	}

	public void setIgnoreUnparsableSubExpressions(ErrorHandling ignoreUnparsableSubExpressions) {
		this.ignoreUnparsableSubExpressions = ignoreUnparsableSubExpressions;
	}

	/**
	 * Checking expression on correct syntax
	 *
	 * @param constraint         constraint (without parenthesis) to convert
	 * @param symbols            array containing strings for: iff, implies, or,
	 *                           and, not
	 * @param quotedFeatureNames list of substituted feature names
	 * @param subExpressions     list of substituted parenthesis expressions
	 * @return
	 */
	private Formula checkExpression(String constraint, List<String> quotedFeatureNames, List<String> subExpressions)
		throws ParseException {
		if (constraint.isEmpty()) {
			errorType.setError(ErrorEnum.Default);
			return handleInvalidExpression(ErrorMessage.EMPTY_EXPRESSION, constraint);
		}
		constraint = SPACE + constraint + SPACE;
		// traverse all symbols
		for (final Operator operator : operators) {
			final String symbol = getSymbols().getSymbol(operator);
			final String symbolPattern = String.format(symbolPatternString, Pattern.quote(symbol));
			final Matcher matcher = Pattern.compile(symbolPattern).matcher(constraint);
			while (matcher.find()) {
				// 1st symbol occurrence
				final int index = matcher.start(1);

				// recursion for children nodes

				final Formula node1, node2;
				if (operator == Operator.NOT) {
					final String rightSide = constraint.substring(index + symbol.length(), constraint.length()).trim();
					node1 = null;
					if (rightSide.isEmpty()) {
						errorType.setError(ErrorEnum.Default);
						node2 = handleInvalidExpression(ErrorMessage.MISSING_NAME, constraint);
					} else {
						node2 = checkExpression(rightSide, quotedFeatureNames, subExpressions);
					}
					if (node2 == null) {
						return null;
					}
				} else {
					final String leftSide = constraint.substring(0, index).trim();
					if (leftSide.isEmpty()) {
						errorType = new ErrorType(ErrorEnum.InvalidExpressionLeft, matcher.start(), matcher.end());
						node1 = handleInvalidExpression(ErrorMessage.MISSING_NAME_LEFT, constraint);
					} else {
						node1 = checkExpression(leftSide, quotedFeatureNames, subExpressions);
					}
					if (node1 == null) {
						return null;
					}
					final String rightSide = constraint.substring(index + symbol.length(), constraint.length()).trim();
					if (rightSide.isEmpty()) {
						errorType = new ErrorType(ErrorEnum.InvalidExpressionRight, matcher.start(), matcher.end());
						node2 = handleInvalidExpression(ErrorMessage.MISSING_NAME_RIGHT, constraint);
					} else {
						node2 = checkExpression(rightSide, quotedFeatureNames, subExpressions);
					}
					if (node2 == null) {
						return null;
					}
				}

				switch (operator) {
				case EQUALS: {
					return new Equals(node1, node2);
				}
				case IMPLIES: {
					return new Implies(node1, node2);
				}
				case OR: {
					return new Or(node1, node2);
				}
				case AND: {
					return new And(node1, node2);
				}
				case NOT: {
					return new Not(node2);
				}
				case ATLEAST:
				case ATMOST:
				case BETWEEN:
				case CHOOSE:
				case EXISTS:
				case FORALL:
				case UNKOWN:
					return null;
				default:
					throw new IllegalStateException(String.valueOf(operator));
				}
			}
		}
		constraint = constraint.trim();
		final Matcher subExpressionMatcher = subExpressionPattern.matcher(constraint);
		if (subExpressionMatcher.find()) {
			if ((subExpressionMatcher.start() == 0) && (subExpressionMatcher.end() == constraint.length())) {
				return checkExpression(subExpressions.get(Integer.parseInt(constraint.substring(1))).trim(),
					quotedFeatureNames, subExpressions);
			} else {
				errorType.setError(ErrorEnum.Default);
				return handleInvalidExpression(ErrorMessage.MISSING_OPERATOR, constraint);
			}
		} else {
			String featureName;
			final Matcher featureNameMatcher = featureNamePattern.matcher(constraint);
			if (featureNameMatcher.find()) {
				if ((featureNameMatcher.start() == 0) && (featureNameMatcher.end() == constraint.length())) {
					featureName = quotedFeatureNames.get(Integer.parseInt(constraint.substring(1)));
				} else {
					errorType.setError(ErrorEnum.Default);
					return handleInvalidExpression(ErrorMessage.MISSING_OPERATOR, constraint);
				}
			} else {
				if (constraint.contains(String.valueOf(SPACE))) {
					errorType = new ErrorType(ErrorEnum.InvalidFeatureName, constraint);
					return handleInvalidFeatureName(constraint);
				}
				featureName = constraint;
			}
			featureName = featureName.replace(replacedFeatureNameMarker, featureNameMarker)
				.replace(replacedSubExpressionMarker, subExpressionMarker);
			if ((featureNames != null) && !featureNames.contains(featureName)) {

				errorType = new ErrorType(ErrorEnum.InvalidFeatureName, featureName);
				return handleInvalidFeatureName(featureName);
			}
			return new Literal(featureName);
		}
	}

	private Formula handleInvalidFeatureName(String featureName) throws ParseException {
		return getInvalidLiteral(ErrorMessage.INVALID_FEATURE_NAME, ignoreMissingFeatures, featureName);
	}

	private Formula handleInvalidExpression(ErrorMessage message, String constraint) throws ParseException {
		return getInvalidLiteral(message, ignoreUnparsableSubExpressions, constraint);
	}

	private Formula getInvalidLiteral(ErrorMessage message, ErrorHandling handleError, String element)
		throws ParseException {
		switch (handleError) {
		case KEEP:
			return new ErrorLiteral(element);
		case REMOVE:
			return null;
		case ABORT:
		default:
			throwParsingError(message, 0, element);
			return null;
		}
	}

	private Formula parseNode(String constraint) throws ParseException {
		constraint = constraint.trim();
		if (constraint.isEmpty()) {
			throwParsingError(ErrorMessage.EMPTY_CONSTRAINT, 0);
		}

		int parenthesisCounter = 0;
		boolean quoteSign = false;
		for (int i = 0; i < constraint.length(); i++) {
			final char curChar = constraint.charAt(i);
			switch (curChar) {
			case PARENTHESIS_OPEN:
				if (quoteSign) {
					errorType.setError(ErrorEnum.Default);
					throwParsingError(ErrorMessage.PARENTHESES_IN_FEATURE_NAMES, i);
				}
				parenthesisCounter++;
				break;
			case QUOTE:
				quoteSign = !quoteSign;
				break;
			case PARENTHESIS_CLOSE:
				if (quoteSign) {
					errorType.setError(ErrorEnum.Default);
					throwParsingError(ErrorMessage.PARENTHESES_IN_FEATURE_NAMES, i);
				}
				if (--parenthesisCounter < 0) {
					errorType.setError(ErrorEnum.Default);
					throwParsingError(ErrorMessage.INVALID_CLOSING_PARENTHESES, i);
				}
				break;
			default:
				break;
			}
		}
		if (quoteSign) {
			throwParsingError(ErrorMessage.INVALID_NUMBER_OF_QUOTATION_MARKS, 0);
		}
		if (parenthesisCounter > 0) {
			errorType.setError(ErrorEnum.Default);
			throwParsingError(ErrorMessage.INVALID_OPENING_PARENTHESES, 0);
		}

		constraint = constraint.replace(featureNameMarker, replacedFeatureNameMarker);
		constraint = constraint.replace(subExpressionMarker, replacedSubExpressionMarker);

		final ArrayList<String> quotedFeatureNames = new ArrayList<>();
		final ArrayList<String> subExpressions = new ArrayList<>();
		if (constraint.contains(String.valueOf(QUOTE))) {
			constraint = replaceGroup(constraint, featureNameMarker, quotedFeatureNames, quotePattern);
		}
		while (constraint.contains(String.valueOf(PARENTHESIS_OPEN))) {
			constraint = replaceGroup(constraint, subExpressionMarker, subExpressions, parenthesisPattern);
		}

		return checkExpression(constraint, quotedFeatureNames, subExpressions);
	}

	private String replaceGroup(String constraint, String marker, final List<String> groupList, final Pattern pattern) {
		int counter = groupList.size();

		final ArrayList<Integer> positionList = new ArrayList<>();
		final Matcher matcher = pattern.matcher(constraint);
		positionList.add(0);
		while (matcher.find()) {
			groupList.add(matcher.group(1));
			positionList.add(matcher.start());
			positionList.add(matcher.end());
		}
		positionList.add(constraint.length());

		final StringBuilder sb = new StringBuilder(constraint.substring(positionList.get(0), positionList.get(1)));
		for (int i = 2; i < positionList.size(); i += 2) {
			sb.append(marker);
			sb.append(counter++);
			sb.append(constraint.substring(positionList.get(i), positionList.get(i + 1)));
		}
		return sb.toString();
	}

}
