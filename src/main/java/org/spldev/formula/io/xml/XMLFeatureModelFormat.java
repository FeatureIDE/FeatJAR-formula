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

import org.spldev.formula.structure.Formula;
import org.spldev.formula.structure.atomic.literal.Literal;
import org.spldev.formula.structure.atomic.literal.LiteralPredicate;
import org.spldev.formula.structure.atomic.literal.VariableMap;
import org.spldev.formula.structure.compound.And;
import org.spldev.formula.structure.compound.Or;
import org.spldev.formula.structure.term.bool.BoolVariable;
import org.spldev.util.io.format.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Parses feature model formulas from FeatureIDE XML files.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelFormat extends AbstractXMLFeatureModelFormat<Formula, Literal, Boolean> {
	protected final List<Formula> constraints = new ArrayList<>();
	protected final VariableMap variableMap = VariableMap.emptyMap();

	@Override
	public XMLFeatureModelFormat getInstance() {
		return new XMLFeatureModelFormat();
	}

	@Override
	public String getName() {
		return "FeatureIDE";
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	protected Formula parseDocument(Document document) throws ParseException {
		final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
		parseFeatureTree(getElement(featureModelElement, STRUCT));
		Optional<Element> constraintsElement = getOptionalElement(featureModelElement, CONSTRAINTS);
		if (constraintsElement.isPresent())
			parseConstraints(constraintsElement.get(), variableMap::getVariable);
		if (constraints.isEmpty()) {
			return And.empty(variableMap);
		} else {
			if (constraints.get(0).getChildren().isEmpty()) {
				constraints.set(0, Or.empty(variableMap));
			}
		}
		return new And(constraints);
	}

	@Override
	protected void writeDocument(Formula object, Document doc) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Pattern getInputHeaderPattern() {
		return AbstractXMLFeatureModelFormat.inputHeaderPattern;
	}

	@Override
	protected Literal createFeatureLabel(String name, Literal parentFeatureLabel, boolean mandatory, boolean _abstract,
		boolean hidden)
		throws ParseException {
		if (variableMap.getIndex(name).isEmpty()) {
			variableMap.addBooleanVariable(name);
		} else {
			throw new ParseException("Duplicate feature name!");
		}

		Literal literal = new LiteralPredicate((BoolVariable) variableMap.getVariable(name).get(), true);
		if (parentFeatureLabel == null) {
			constraints.add(literal);
		} else {
			constraints.add(implies(literal, parentFeatureLabel));
			if (mandatory) {
				constraints.add(implies(parentFeatureLabel, literal));
			}
		}
		return literal;
	}

	@Override
	protected void addAndGroup(Literal featureLabel, List<Literal> childFeatureLabels) {
	}

	@Override
	protected void addOrGroup(Literal featureLabel, List<Literal> childFeatureLabels) {
		constraints.add(implies(featureLabel, childFeatureLabels));
	}

	@Override
	protected void addAlternativeGroup(Literal featureLabel, List<Literal> childFeatureLabels) {
		if (childFeatureLabels.size() == 1) {
			constraints.add(implies(featureLabel, childFeatureLabels.get(0)));
		} else {
			constraints.add(new And(implies(featureLabel, childFeatureLabels), atMostOne(childFeatureLabels)));
		}
	}

	@Override
	protected void addFeatureMetadata(Literal featureLabel, Element e) {
	}

	@Override
	protected Boolean createConstraintLabel() {
		return true;
	}

	@Override
	protected void addConstraint(Boolean constraintLabel, Formula formula) throws ParseException {
		constraints.add(formula);
	}

	@Override
	protected void addConstraintMetadata(Boolean constraintLabel, Element e) {
	}
}
