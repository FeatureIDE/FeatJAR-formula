/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
package de.featjar.formula.io.xml;

import de.featjar.base.data.Result;
import de.featjar.base.data.Sets;
import de.featjar.base.io.format.ParseException;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Or;
import de.featjar.formula.structure.connective.Reference;
import de.featjar.formula.structure.predicate.Literal;
import de.featjar.formula.structure.term.value.Variable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses feature model formulas from FeatureIDE XML files.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelFormulaFormat extends AXMLFeatureModelFormat<IFormula, Literal, Boolean> {
    protected final LinkedHashSet<String> featureLabels = Sets.empty();
    protected final List<IFormula> constraints = new ArrayList<>();

    @Override
    public XMLFeatureModelFormulaFormat getInstance() {
        return new XMLFeatureModelFormulaFormat();
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
    protected IFormula parseDocument(Document document) throws ParseException {
        final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL, EXT_FEATURE_MODEL);
        parseFeatureTree(getElement(featureModelElement, STRUCT));
        Result<Element> constraintsElement = getElementResult(featureModelElement, CONSTRAINTS);
        if (constraintsElement.isPresent()) parseConstraints(constraintsElement.get());
        if (constraints.isEmpty()) {
            return new And();
        } else {
            if (constraints.get(0).getChildren().isEmpty()) {
                constraints.set(0, new Or());
            }
        }
        Reference reference = new Reference(constraints.size() == 1 ? constraints.get(0) : new And(constraints));
        reference.setFreeVariables(featureLabels.stream().map(Variable::new).collect(Collectors.toList()));
        return reference;
    }

    @Override
    protected void writeDocument(IFormula object, Document doc) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Pattern getInputHeaderPattern() {
        return AXMLFeatureModelFormat.inputHeaderPattern;
    }

    @Override
    protected Literal newFeatureLabel(
            String name, Literal parentFeatureLabel, boolean mandatory, boolean _abstract, boolean hidden)
            throws ParseException {
        if (featureLabels.contains(name)) {
            throw new ParseException("Duplicate feature name!");
        } else {
            featureLabels.add(name);
        }
        Literal literal = new Literal(name);
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
    protected void addAndGroup(Literal featureLabel, List<Literal> childFeatureLabels) {}

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
    protected void addFeatureMetadata(Literal featureLabel, Element e) {}

    @Override
    protected Boolean newConstraintLabel() {
        return true;
    }

    @Override
    protected void addConstraint(Boolean constraintLabel, IFormula formula) throws ParseException {
        constraints.add(formula);
    }

    @Override
    protected void addConstraintMetadata(Boolean constraintLabel, Element e) {}
}
