package org.spldev.formula.expression.io.parse;

import java.util.*;
import java.util.regex.*;

import org.spldev.formula.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.io.parse.ErrorType.*;
import org.spldev.formula.expression.io.parse.Symbols.*;
import org.spldev.util.*;
import org.spldev.util.Problem.*;
import org.spldev.util.io.format.*;

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
		EMPTY_EXPRESSION("Sub expression is empty."),
		MISSING_NAME("Missing feature name or expression: %s"),
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

	private final void throwParsingError(ErrorMessage message, int offset, Object... context) throws ParseException {
		throw new ParseException(String.format(message.getMessage(), context), offset);
	}

	public enum ErrorHandling {
		THROW, REMOVE, KEEP
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

	private VariableMap map;
	private boolean hasVariableNames;

	private Symbols symbols = ShortSymbols.INSTANCE;

	public ErrorType errorType = new ErrorType(ErrorEnum.None);

	private ErrorHandling ignoreMissingFeatures = ErrorHandling.THROW;
	private ErrorHandling ignoreUnparsableSubExpressions = ErrorHandling.THROW;
	private List<Problem> problemList;

	public VariableMap getFeatureNames() {
		return map;
	}

	public Symbols getSymbols() {
		return symbols;
	}

	public void setSymbols(Symbols symbols) {
		this.symbols = symbols;
	}

	public void setVariableNames(Collection<String> variableNames) {
		if (variableNames == null) {
			map = new VariableMap();
			hasVariableNames = false;
		} else {
			map = new VariableMap(variableNames);
			hasVariableNames = true;
		}
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
	 * Parses a constraint and create a corresponding {@link Formula}.
	 *
	 * @param formulaString The constraint as a string representation
	 * @return The parsed formula
	 */
	public Result<Formula> read(String formulaString) {
		problemList = new ArrayList<>();
		if (formulaString == null) {
			return Result.empty(new ParseProblem(new ParseException(ErrorMessage.NULL_CONSTRAINT.getMessage(), 0),
				0));
		}
		try {
			return Result.of(parseNode(formulaString));
		} catch (final ParseException e) {
			problemList.add(new ParseProblem(e, 0));
			switch (ignoreUnparsableSubExpressions) {
			case KEEP:
				return Result.of(new ErrorLiteral(formulaString));
			case REMOVE:
			case THROW:
				return Result.empty(problemList);
			default:
				throw new IllegalStateException(String.valueOf(ignoreUnparsableSubExpressions));
			}
		}
	}

	private Formula checkExpression(String source, List<String> quotedVariables, List<String> subExpressions)
		throws ParseException {
		if (source.isEmpty()) {
			errorType.setError(ErrorEnum.Default);
			return handleInvalidExpression(ErrorMessage.EMPTY_EXPRESSION, source);
		}
		source = SPACE + source + SPACE;
		// traverse all symbols
		for (final Operator operator : operators) {
			final String symbol = getSymbols().getSymbol(operator);
			final String symbolPattern = String.format(symbolPatternString, Pattern.quote(symbol));
			final Matcher matcher = Pattern.compile(symbolPattern).matcher(source);
			while (matcher.find()) {
				// 1st symbol occurrence
				final int index = matcher.start(1);

				// recursion for children nodes

				final Formula node1, node2;
				if (operator == Operator.NOT) {
					final String rightSide = source.substring(index + symbol.length(), source.length()).trim();
					node1 = null;
					if (rightSide.isEmpty()) {
						errorType.setError(ErrorEnum.Default);
						node2 = handleInvalidExpression(ErrorMessage.MISSING_NAME, source);
					} else {
						node2 = checkExpression(rightSide, quotedVariables, subExpressions);
					}
					if (node2 == null) {
						return null;
					}
				} else {
					final String leftSide = source.substring(0, index).trim();
					if (leftSide.isEmpty()) {
						errorType = new ErrorType(ErrorEnum.InvalidExpressionLeft, matcher.start(), matcher.end());
						node1 = handleInvalidExpression(ErrorMessage.MISSING_NAME_LEFT, source);
					} else {
						node1 = checkExpression(leftSide, quotedVariables, subExpressions);
					}
					if (node1 == null) {
						return null;
					}
					final String rightSide = source.substring(index + symbol.length(), source.length()).trim();
					if (rightSide.isEmpty()) {
						errorType = new ErrorType(ErrorEnum.InvalidExpressionRight, matcher.start(), matcher.end());
						node2 = handleInvalidExpression(ErrorMessage.MISSING_NAME_RIGHT, source);
					} else {
						node2 = checkExpression(rightSide, quotedVariables, subExpressions);
					}
					if (node2 == null) {
						return null;
					}
				}

				switch (operator) {
				case EQUALS: {
					return new Biimplies(node1, node2);
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
		source = source.trim();
		final Matcher subExpressionMatcher = subExpressionPattern.matcher(source);
		if (subExpressionMatcher.find()) {
			if ((subExpressionMatcher.start() == 0) && (subExpressionMatcher.end() == source.length())) {
				return checkExpression(subExpressions.get(Integer.parseInt(source.substring(1))).trim(),
					quotedVariables, subExpressions);
			} else {
				errorType.setError(ErrorEnum.Default);
				return handleInvalidExpression(ErrorMessage.MISSING_OPERATOR, source);
			}
		} else {
			String featureName;
			final Matcher featureNameMatcher = featureNamePattern.matcher(source);
			if (featureNameMatcher.find()) {
				if ((featureNameMatcher.start() == 0) && (featureNameMatcher.end() == source.length())) {
					featureName = quotedVariables.get(Integer.parseInt(source.substring(1)));
				} else {
					errorType.setError(ErrorEnum.Default);
					return handleInvalidExpression(ErrorMessage.MISSING_OPERATOR, source);
				}
			} else {
				if (source.contains(String.valueOf(SPACE))) {
					errorType = new ErrorType(ErrorEnum.InvalidFeatureName, source);
					return handleInvalidFeatureName(source);
				}
				featureName = source;
			}
			featureName = featureName.replace(replacedFeatureNameMarker, featureNameMarker)
				.replace(replacedSubExpressionMarker, subExpressionMarker);
			if (hasVariableNames && map.getIndex(featureName).isEmpty()) {
				errorType = new ErrorType(ErrorEnum.InvalidFeatureName, featureName);
				return handleInvalidFeatureName(featureName);
			}
			if (map.getIndex(featureName).isEmpty()) {
				map.addVariable(featureName);
			}

			return new LiteralVariable(featureName, map);
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
			problemList.add(new ParseProblem(message.getMessage(), 0, Severity.WARNING));
			return new ErrorLiteral(element);
		case REMOVE:
			problemList.add(new ParseProblem(message.getMessage(), 0, Severity.WARNING));
			return null;
		case THROW:
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
