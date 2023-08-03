/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.io.xml;

import static de.featjar.base.computation.Computations.*;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.ParseException;
import de.featjar.base.tree.Trees;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.transformer.ComputeCNFFormula;
import de.featjar.formula.transformer.ComputeNNFFormula;
import de.featjar.formula.visitor.AndOrSimplifier;
import de.featjar.formula.visitor.ConnectiveSimplifier;
import de.featjar.formula.visitor.DeMorganApplier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    protected IExpression parseDocument(Document document) throws ParseException {
        final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
        parseFeatureTree(getElement(featureModelElement, STRUCT));
        Result<Element> constraintsElement = getElementResult(featureModelElement, CONSTRAINTS);
        if (constraintsElement.isPresent()) {
            parseConstraints(constraintsElement.get());
        }
        return Trees.clone(simplify(new And(constraints)));
    }

    @Override
    protected void addConstraint(Boolean constraintLabel, IFormula formula) throws ParseException {
        IFormula transformedExpression = async(formula)
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .computeUncachedResult()
                .orElseThrow(p -> new ParseException("failed to transform " + formula));
        super.addConstraint(constraintLabel, transformedExpression);
    }

    @Override
    protected IFormula atMostOne(List<? extends IFormula> parseFeatures) {
        return new And(ConnectiveSimplifier.groupElements(
                parseFeatures.stream().map(Not::new).collect(Collectors.toList()), 1, parseFeatures.size()));
    }

    @Override
    protected IFormula biImplies(IFormula a, IFormula b) {
        return new And(new Or(new Not(a), b), new Or(new Not(b), a));
    }

    @Override
    protected IFormula implies(Literal a, IFormula b) {
        return new Or(a.invert(), b);
    }

    @Override
    protected IFormula implies(IFormula a, IFormula b) {
        return new Or(new Not(a), b);
    }

    @Override
    protected IFormula implies(Literal f, List<? extends IFormula> parseFeatures) {
        final ArrayList<IFormula> list = new ArrayList<>(parseFeatures.size() + 1);
        list.add(f.invert());
        list.addAll(parseFeatures);
        return new Or(list);
    }

    private static IFormula simplify(IFormula formula) {
        // TODO: error handling
        Trees.traverse(formula, new DeMorganApplier());
        Trees.traverse(formula, new AndOrSimplifier());
        return formula;
    }
}
