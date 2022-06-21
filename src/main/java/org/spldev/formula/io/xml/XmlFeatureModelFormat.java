/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.io.xml;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.parsers.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.structure.term.Constant;
import org.spldev.formula.structure.term.Variable;
import org.spldev.formula.structure.term.bool.*;
import org.spldev.util.data.*;
import org.spldev.util.data.Problem.*;
import org.spldev.util.io.*;
import org.spldev.util.io.format.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class XmlFeatureModelFormat implements Format<Formula> {

	public static final String ID = XmlFeatureModelFormat.class.getCanonicalName();

	public final static String FEATURE_MODEL = "featureModel";
	public final static String STRUCT = "struct";
	public final static String CONSTRAINTS = "constraints";
	public final static String TRUE = "true";
	public final static String MANDATORY = "mandatory";
	public final static String FEATURE = "feature";
	public final static String OR = "or";
	public final static String ALT = "alt";
	public final static String AND = "and";
	public final static String VAR = "var";
	public final static String IMP = "imp";
	public final static String EQ = "eq";
	public final static String NOT = "not";
	public final static String CONJ = "conj";
	public final static String DISJ = "disj";
	public final static String NAME = "name";
	public final static String RULE = "rule";
	public final static String ATMOST1 = "atmost1";

	protected ArrayList<Formula> constraints = new ArrayList<>();
	protected List<Problem> parseProblems = new ArrayList<>();
	protected VariableMap map;

	public XmlFeatureModelFormat() {
	}

	/**
	 * Returns a list of elements within the given node list.
	 *
	 * @param nodeList the node list.
	 * @return The child nodes from type Element of the given NodeList.
	 */
	public static List<Element> getElements(NodeList nodeList) {
		final ArrayList<Element> elements = new ArrayList<>(nodeList.getLength());
		for (int temp = 0; temp < nodeList.getLength(); temp++) {
			final org.w3c.dom.Node nNode = nodeList.item(temp);
			if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				final Element eElement = (Element) nNode;
				elements.add(eElement);
			}
		}
		return elements;
	}

	public static List<Element> getElement(final Element element, final String nodeName) {
		return getElements(element.getElementsByTagName(nodeName));
	}

	public static List<Element> getElement(final Document document, final String nodeName) {
		return getElements(document.getElementsByTagName(nodeName));
	}

	@Override
	public String getFileExtension() {
		return "xml";
	}

	@Override
	public Result<Formula> parse(Input source) {
		try {
			parseProblems.clear();
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(source.getReader()),
				new PositionalXMLHandler(doc));
			doc.getDocumentElement().normalize();
			return Result.of(readDocument(doc), parseProblems);
		} catch (final Exception e) {
			return Result.empty(new Problem(e));
		}
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	protected Formula readDocument(Document doc) throws ParseException {
		map = VariableMap.emptyMap();
		final List<Element> elementList = getElement(doc, FEATURE_MODEL);
		if (elementList.size() == 1) {
			final Element e = elementList.get(0);
			parseStruct(getElement(e, STRUCT));
			parseConstraints(getElement(e, CONSTRAINTS));
		} else if (elementList.isEmpty()) {
			throw new ParseException("Not a feature model xml element!");
		} else {
			throw new ParseException("More than one feature model xml elements!");
		}
		if (constraints.isEmpty()) {
			return And.empty(map);
		} else {
			if (constraints.get(0).getChildren().isEmpty()) {
				constraints.set(0, Or.empty(map));
			}
		}
		return new And(constraints);
	}

	public void parseConstraints(List<Element> elements, Function<String, Optional<Variable<?>>> variableFunction, List<Problem> parseProblems, Consumer<Formula> formulaConsumer) throws ParseException {
		if (elements.size() > 1) {
			throw new ParseException("Multiple <constraints> elements!");
		}
		for (final Element e : elements) {
			for (final Element child : getElements(e.getChildNodes())) {
				final String nodeName = child.getNodeName();
				if (nodeName.equals(RULE)) {
					try {
						final List<Formula> parseConstraintNode = parseConstraintNode(child.getChildNodes(), variableFunction);
						if (parseConstraintNode.size() == 1) {
							formulaConsumer.accept(parseConstraintNode.get(0));
						} else {
							parseProblems.add(new ParseProblem(nodeName,
									(int) child.getUserData(PositionalXMLHandler.LINE_NUMBER_KEY_NAME), Severity.WARNING));
						}
					} catch (final Exception exception) {
						parseProblems.add(new ParseProblem(exception.getMessage(),
								(int) child.getUserData(PositionalXMLHandler.LINE_NUMBER_KEY_NAME), Severity.WARNING));
					}
				}
			}
		}
	}

	protected void parseConstraints(List<Element> elements) throws ParseException {
		parseConstraints(elements, map::getVariable, parseProblems, constraints::add);
	}

	protected List<Formula> parseConstraintNode(NodeList nodeList, Function<String, Optional<Variable<?>>> variableFunction) throws ParseException {
		final List<Formula> nodes = new ArrayList<>();
		List<Formula> children;
		final List<Element> elements = getElements(nodeList);
		for (final Element e : elements) {
			final String nodeName = e.getNodeName();
			switch (nodeName) {
			case DISJ:
				children = parseConstraintNode(e.getChildNodes(), variableFunction);
				if (!children.isEmpty()) {
					nodes.add(new Or(children));
				}
				break;
			case CONJ:
				children = parseConstraintNode(e.getChildNodes(), variableFunction);
				if (!children.isEmpty()) {
					nodes.add(new And(children));
				}
				break;
			case EQ:
				children = parseConstraintNode(e.getChildNodes(), variableFunction);
				if (children.size() == 2) {
					nodes.add(biimplies(children.get(0), children.get(1)));
				}
				break;
			case IMP:
				children = parseConstraintNode(e.getChildNodes(), variableFunction);
				nodes.add(implies(children.get(0), children.get(1)));
				break;
			case NOT:
				children = parseConstraintNode( e.getChildNodes(), variableFunction);
				if (children.size() == 1) {
					nodes.add(new Not(children.get(0)));
				}
				break;
			case ATMOST1:
				children = parseConstraintNode(e.getChildNodes(), variableFunction);
				if (!children.isEmpty()) {
					nodes.add(atMost(children));
				}
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

	protected ArrayList<Formula> parseFeatures(NodeList nodeList, Literal parent, boolean and) throws ParseException {
		final ArrayList<Formula> children = new ArrayList<>();
		final List<Element> elements = getElements(nodeList);
		if (parent == null) {
			if (elements.isEmpty()) {
				throw new ParseException("No root feature!");
			}
			if (elements.size() > 1) {
				throw new ParseException("Multiple root features!");
			}
		} else {
			if (elements.isEmpty()) {
				parseProblems.add(new ParseProblem("No feature in group!", 0, Severity.WARNING));
			}
		}
		for (final Element e : elements) {
			final String nodeName = e.getNodeName();
			switch (nodeName) {
			case AND:
			case OR:
			case ALT:
			case FEATURE:
				children.add(parseFeature(parent, e, nodeName, and));
				break;
			default:
				break;
			}
		}
		return children;
	}

	protected LiteralPredicate parseFeature(Literal parent, final Element e, final String nodeName, boolean and)
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
		if (map.getIndex(name).isEmpty()) {
			map.addBooleanVariable(name);
		} else {
			throw new ParseException("Duplicate feature name!");
		}

		final LiteralPredicate f = new LiteralPredicate((BoolVariable) map.getVariable(name).get(), true);

		if (parent == null) {
			constraints.add(f);
		} else {
			constraints.add(implies(f, parent));
			if (and && mandatory) {
				constraints.add(implies(parent, f));
			}
		}

		if (e.hasChildNodes()) {
			final ArrayList<Formula> parseFeatures = parseFeatures(e.getChildNodes(), f, nodeName.equals(AND));
			switch (nodeName) {
			case AND:
				break;
			case OR:
				constraints.add(implies(f, parseFeatures));
				break;
			case ALT:
				if (parseFeatures.size() == 1) {
					constraints.add(implies(f, parseFeatures.get(0)));
				} else {
					constraints.add(new And(implies(f, parseFeatures), atMost(parseFeatures)));
				}
				break;
			default:
				break;
			}
		} else if (!"feature".equals(nodeName)) {
			throw new ParseException("Empty group!");
		}

		return f;
	}

	protected Formula atMost(final List<Formula> parseFeatures) {
		return new AtMost(parseFeatures, 1);
	}

	protected Formula biimplies(Formula a, final Formula b) {
		return new Biimplies(a, b);
	}

	protected Formula implies(Literal a, final Formula b) {
		return new Implies(a, b);
	}

	protected Formula implies(Formula a, final Formula b) {
		return new Implies(a, b);
	}

	protected Formula implies(final LiteralPredicate f, final List<Formula> parseFeatures) {
		return parseFeatures.size() == 1
			? new Implies(f, parseFeatures.get(0))
			: new Implies(f, new Or(parseFeatures));
	}

	protected void parseStruct(List<Element> elements) throws ParseException {
		if (elements.isEmpty()) {
			throw new ParseException("No <struct> element!");
		}
		if (elements.size() > 1) {
			throw new ParseException("Multiple <struct> elements!");
		}
		parseFeatures(elements.get(0).getChildNodes(), null, false);
	}

	@Override
	public XmlFeatureModelFormat getInstance() {
		return new XmlFeatureModelFormat();
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public String getName() {
		return "FeatureIDE";
	}

}
