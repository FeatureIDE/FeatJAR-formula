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
package de.featjar.formula.io.xml;

import de.featjar.base.data.Computation;
import de.featjar.base.data.Computations;
import de.featjar.base.io.format.ParseException;
import de.featjar.base.tree.Trees;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.transformer.ComputeCNFFormula;
import de.featjar.formula.transformer.ComputeNNFFormula;
import de.featjar.formula.visitor.AndOrSimplifier;
import de.featjar.formula.visitor.ConnectiveSimplifier;
import de.featjar.formula.visitor.DeMorganApplier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.featjar.base.data.Computations.*;

/**
 * Parses feature model CNF formulas from FeatureIDE XML files. Returns a
 * formula that is already partially in CNF, except for cross-tree constraints. TODO: actually, this actively transforms...?
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelCNFFormulaFormat extends XMLFeatureModelFormulaFormat {
    @Override
    public XMLFeatureModelCNFFormulaFormat getInstance() {
        return new XMLFeatureModelCNFFormulaFormat();
    }

    @Override
    public String getName() {
        return "FeatureIDECNF";
    }

    @Override
    protected Expression parseDocument(Document document) throws ParseException {
        final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
        parseFeatureTree(getElement(featureModelElement, STRUCT));
        Optional<Element> constraintsElement = getOptionalElement(featureModelElement, CONSTRAINTS);
        if (constraintsElement.isPresent()) {
            parseConstraints(constraintsElement.get());
        }
        return Trees.clone(simplify(new And(constraints)));
    }

    @Override
    protected void addConstraint(Boolean constraintLabel, Formula formula) throws ParseException {
        Formula transformedExpression = async(formula).map(ComputeNNFFormula::new).map(ComputeCNFFormula::new).getResult()
                .orElseThrow(p -> new ParseException("failed to transform " + formula));
        super.addConstraint(constraintLabel, transformedExpression);
    }

    @Override
    protected Formula atMostOne(List<? extends Formula> parseFeatures) {
        return new And(ConnectiveSimplifier.groupElements(
                parseFeatures.stream().map(Not::new).collect(Collectors.toList()), 1, parseFeatures.size()));
    }

    @Override
    protected Formula biImplies(Formula a, Formula b) {
        return new And(new Or(new Not(a), b), new Or(new Not(b), a));
    }

    @Override
    protected Formula implies(Literal a, Formula b) {
        return new Or(a.invert(), b);
    }

    @Override
    protected Formula implies(Formula a, Formula b) {
        return new Or(new Not(a), b);
    }

    @Override
    protected Formula implies(Literal f, List<? extends Formula> parseFeatures) {
        final ArrayList<Formula> list = new ArrayList<>(parseFeatures.size() + 1);
        list.add(f.invert());
        list.addAll(parseFeatures);
        return new Or(list);
    }

    private static Formula simplify(Formula formula) {
        // TODO: error handling
        Trees.traverse(formula, new DeMorganApplier());
        Trees.traverse(formula, new AndOrSimplifier());
        return formula;
    }
}
