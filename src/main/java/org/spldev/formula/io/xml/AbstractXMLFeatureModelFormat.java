package org.spldev.formula.io.xml;

import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.atomic.literal.ErrorLiteral;
import org.spldev.formula.structure.atomic.literal.Literal;
import org.spldev.formula.structure.atomic.literal.LiteralPredicate;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.structure.term.Variable;
import org.spldev.formula.structure.term.bool.BoolVariable;
import org.spldev.util.data.Problem;
import org.spldev.util.io.format.ParseException;
import org.spldev.util.io.xml.XMLFormat;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implements common behaviours for parsing and writing XML feature model files.
 *
 * @param <T> type of read/written data
 * @param <U> type of feature labels
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class AbstractXMLFeatureModelFormat<T, U> extends XMLFormat<T> {
	protected static final Pattern inputHeaderPattern = Pattern.compile(
		"\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<featureModel[\\s>]");
	protected static final String FEATURE_MODEL = "featureModel";
	protected static final String STRUCT = "struct";
	protected static final String CONSTRAINTS = "constraints";
	protected static final String TRUE = "true";
	protected static final String MANDATORY = "mandatory";
	protected static final String FEATURE = "feature";
	protected static final String OR = "or";
	protected static final String ALT = "alt";
	protected static final String AND = "and";
	protected static final String VAR = "var";
	protected static final String IMP = "imp";
	protected static final String EQ = "eq";
	protected static final String NOT = "not";
	protected static final String CONJ = "conj";
	protected static final String DISJ = "disj";
	protected static final String NAME = "name";
	protected static final String RULE = "rule";
	protected static final String ATMOST1 = "atmost1";

	abstract protected U createFeatureLabel(String name, U parentFeatureLabel, boolean mandatory) throws ParseException;

	abstract protected void addAndGroup(U featureLabel, List<U> childFeatureLabels);

	abstract protected void addOrGroup(U featureLabel, List<U> childFeatureLabels);

	abstract protected void addAlternativeGroup(U featureLabel, List<U> childFeatureLabels);

	protected void parseFeatures(Element element) throws ParseException {
		parseFeatures(element.getChildNodes(), null, false);
	}

	protected ArrayList<U> parseFeatures(NodeList nodeList, U parentFeatureLabel, boolean and) throws ParseException {
		final ArrayList<U> featureLabels = new ArrayList<>();
		final List<Element> elements = getElements(nodeList);
		if (parentFeatureLabel == null) {
			if (elements.isEmpty()) {
				throw new ParseException("No root feature!");
			}
			if (elements.size() > 1) {
				throw new ParseException("Multiple root features!");
			}
		} else {
			if (elements.isEmpty()) {
				addParseProblem("No feature in group!", null, Problem.Severity.WARNING);
			}
		}
		for (final Element e : elements) {
			final String nodeName = e.getNodeName();
			switch (nodeName) {
			case AND:
			case OR:
			case ALT:
			case FEATURE:
				featureLabels.add(parseFeature(parentFeatureLabel, e, nodeName, and));
				break;
			default:
				break;
			}
		}
		return featureLabels;
	}

	protected U parseFeature(U parentFeatureLabel, final Element e, final String nodeName, boolean and)
		throws ParseException {
		boolean mandatory = false;
		String name = null;
		if (e.hasAttributes()) {
			final NamedNodeMap nodeMap = e.getAttributes();
			for (int i = 0; i < nodeMap.getLength(); i++) {
				final org.w3c.dom.Node node = nodeMap.item(i);
				final String attributeName = node.getNodeName();
				final String attributeValue = node.getNodeValue();
				if (attributeName.equals(MANDATORY)) {
					mandatory = attributeValue.equals(TRUE);
				} else if (attributeName.equals(NAME)) {
					name = attributeValue;
				}
			}
		}

		U featureLabel = createFeatureLabel(name, parentFeatureLabel, and && mandatory);

		if (e.hasChildNodes()) {
			final ArrayList<U> featureLabels = parseFeatures(e.getChildNodes(), featureLabel, nodeName.equals(AND));
			switch (nodeName) {
			case AND:
				addAndGroup(featureLabel, featureLabels);
				break;
			case OR:
				addOrGroup(featureLabel, featureLabels);
				break;
			case ALT:
				addAlternativeGroup(featureLabel, featureLabels);
				break;
			default:
				break;
			}
		} else if (!"feature".equals(nodeName)) {
			throw new ParseException("Empty group!");
		}

		return featureLabel;
	}

	protected List<Formula> parseConstraints(Element constraintsElement,
		Function<String, Optional<Variable<?>>> variableFunction) throws ParseException {
		List<Formula> constraints = new ArrayList<>();
		for (final Element child : getElements(constraintsElement.getChildNodes())) {
			final String nodeName = child.getNodeName();
			if (nodeName.equals(RULE)) {
				try {
					final List<Formula> parsedConstraints = parseConstraints(child.getChildNodes(),
						variableFunction);
					if (parsedConstraints.size() == 1) {
						constraints.add(parsedConstraints.get(0));
					} else {
						addParseProblem(nodeName, child, Problem.Severity.WARNING);
					}
				} catch (final Exception exception) {
					addParseProblem(exception.getMessage(), child, Problem.Severity.WARNING);
				}
			} else {
				addParseProblem("Encountered unknown node " + nodeName, child, Problem.Severity.WARNING);
			}
		}
		return constraints;
	}

	protected List<Formula> parseConstraints(NodeList nodeList,
		Function<String, Optional<Variable<?>>> variableFunction) throws ParseException {
		final List<Formula> nodes = new ArrayList<>();
		List<Formula> children;
		final List<Element> elements = getElements(nodeList);
		for (final Element e : elements) {
			final String nodeName = e.getNodeName();
			switch (nodeName) {
			case DISJ:
				nodes.add(new Or(parseConstraints(e.getChildNodes(), variableFunction)));
				break;
			case CONJ:
				nodes.add(new And(parseConstraints(e.getChildNodes(), variableFunction)));
				break;
			case EQ:
				children = parseConstraints(e.getChildNodes(), variableFunction);
				if (children.size() == 2) {
					nodes.add(biImplies(children.get(0), children.get(1)));
				} else {
					addParseProblem("unexpected number of operands for equivalence", e, Problem.Severity.WARNING);
				}
				break;
			case IMP:
				children = parseConstraints(e.getChildNodes(), variableFunction);
				if (children.size() == 2) {
					nodes.add(implies(children.get(0), children.get(1)));
				} else {
					addParseProblem("unexpected number of operands for implication", e, Problem.Severity.WARNING);
				}
				break;
			case NOT:
				children = parseConstraints(e.getChildNodes(), variableFunction);
				if (children.size() == 1) {
					nodes.add(new Not(children.get(0)));
				} else {
					addParseProblem("unexpected number of operands for negation", e, Problem.Severity.WARNING);
				}
				break;
			case ATMOST1:
				nodes.add(atMostOne(parseConstraints(e.getChildNodes(), variableFunction)));
				break;
			case VAR:
				nodes.add(variableFunction.apply(e.getTextContent())
					.map(v -> (Literal) new LiteralPredicate((BoolVariable) v, true))
					.orElse(new ErrorLiteral(nodeName)));
				break;
			default:
				throw new ParseException(nodeName);
			}
		}
		return nodes;
	}

	protected Formula atMostOne(List<? extends Formula> parseFeatures) {
		return new AtMost(parseFeatures, 1);
	}

	protected Formula biImplies(Formula a, Formula b) {
		return new Biimplies(a, b);
	}

	protected Formula implies(Literal a, Formula b) {
		return new Implies(a, b);
	}

	protected Formula implies(Formula a, Formula b) {
		return new Implies(a, b);
	}

	protected Formula implies(Literal f, List<? extends Formula> parseFeatures) {
		return parseFeatures.size() == 1
			? new Implies(f, parseFeatures.get(0))
			: new Implies(f, new Or(parseFeatures));
	}
}
