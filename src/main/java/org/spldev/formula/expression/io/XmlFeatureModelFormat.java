/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
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
package org.spldev.formula.expression.io;

import java.util.*;

import javax.xml.parsers.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.integer.*;
import org.spldev.util.*;
import org.spldev.util.Problem.*;
import org.spldev.util.io.*;
import org.spldev.util.io.format.*;
import org.spldev.util.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class XmlFeatureModelFormat implements Format<Formula> {

	public static final String ID = XmlFeatureModelFormat.class.getCanonicalName();

	protected final static String FEATURE_MODEL = "featureModel";
	protected final static String STRUCT = "struct";
	protected final static String CONSTRAINTS = "constraints";
	protected final static String TRUE = "true";
	protected final static String MANDATORY = "mandatory";
	protected final static String FEATURE = "feature";
	protected final static String OR = "or";
	protected final static String ALT = "alt";
	protected final static String AND = "and";
	protected final static String VAR = "var";
	protected final static String IMP = "imp";
	protected final static String EQ = "eq";
	protected final static String NOT = "not";
	protected final static String CONJ = "conj";
	protected final static String DISJ = "disj";
	protected final static String NAME = "name";
	protected final static String RULE = "rule";
	protected final static String ATMOST1 = "atmost1";

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
	protected static final List<Element> getElements(NodeList nodeList) {
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

	protected List<Element> getElement(final Element element, final String nodeName) {
		return getElements(element.getElementsByTagName(nodeName));
	}

	protected List<Element> getElement(final Document document, final String nodeName) {
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

	protected Formula readDocument(Document doc) {
		map = VariableMap.emptyMap();
		final List<Element> elementList = getElement(doc, FEATURE_MODEL);
		if (elementList.size() == 1) {
			final Element e = elementList.get(0);
			parseStruct(getElement(e, STRUCT));
			parseConstraints(getElement(e, CONSTRAINTS));
		} else if (elementList.isEmpty()) {
			Logger.logError("Not feature model xml element!");
		} else {
			Logger.logError("More than one feature model xml elements!");
		}
		return new And(constraints);
	}

	protected void parseConstraints(List<Element> elements) {
		for (final Element e : elements) {
			for (final Element child : getElements(e.getChildNodes())) {
				final String nodeName = child.getNodeName();
				if (nodeName.equals(RULE)) {
					try {
						final List<Formula> parseConstraintNode = parseConstraintNode(child.getChildNodes());
						if (parseConstraintNode.size() == 1) {
							constraints.add(parseConstraintNode.get(0));
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

	protected List<Formula> parseConstraintNode(NodeList nodeList) throws ParseException {
		final List<Formula> nodes = new ArrayList<>();
		List<Formula> children;
		final List<Element> elements = getElements(nodeList);
		for (final Element e : elements) {
			final String nodeName = e.getNodeName();
			switch (nodeName) {
			case DISJ:
				children = parseConstraintNode(e.getChildNodes());
				if (!children.isEmpty()) {
					nodes.add(new Or(children));
				}
				break;
			case CONJ:
				children = parseConstraintNode(e.getChildNodes());
				if (!children.isEmpty()) {
					nodes.add(new And(children));
				}
				break;
			case EQ:
				children = parseConstraintNode(e.getChildNodes());
				if (children.size() == 2) {
					nodes.add(biimplies(children.get(0), children.get(1)));
				}
				break;
			case IMP:
				children = parseConstraintNode(e.getChildNodes());
				nodes.add(implies(children.get(0), children.get(1)));
				break;
			case NOT:
				children = parseConstraintNode(e.getChildNodes());
				if (children.size() == 1) {
					nodes.add(new Not(children.get(0)));
				}
				break;
			case ATMOST1:
				children = parseConstraintNode(e.getChildNodes());
				if (!children.isEmpty()) {
					nodes.add(atMost(children));
				}
				break;
			case VAR:
				nodes.add(map.getVariable(e.getTextContent())
					.map(v -> (Literal) new LiteralVariable((BoolVariable) v, true))
					.orElse(new ErrorLiteral(nodeName)));
				break;
			default:
				throw new ParseException(nodeName);
			}
		}
		return nodes;
	}

	protected ArrayList<Formula> parseFeatures(NodeList nodeList, Literal parent, boolean and) {
		final ArrayList<Formula> children = new ArrayList<>();
		for (final Element e : getElements(nodeList)) {
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

	protected LiteralVariable parseFeature(Literal parent, final Element e, final String nodeName, boolean and) {
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
		}

		final LiteralVariable f = new LiteralVariable((BoolVariable) map.getVariable(name).get(), true);

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

	protected Formula implies(final LiteralVariable f, final List<Formula> parseFeatures) {
		return new Implies(f, new Or(parseFeatures));
	}

	protected void parseStruct(List<Element> elements) {
		for (final Element e : elements) {
			parseFeatures(e.getChildNodes(), null, false);
		}
	}

	@Override
	public XmlFeatureModelFormat getInstance() {
		return new XmlFeatureModelFormat();
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "FeatureIDE";
	}

}
