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

import de.featjar.formula.structure.AuxiliaryRoot;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Not;
import de.featjar.formula.structure.compound.Or;
import de.featjar.formula.structure.transform.CNFTransformer;
import de.featjar.formula.structure.transform.DeMorganVisitor;
import de.featjar.formula.structure.transform.TreeSimplifier;
import de.featjar.formula.structure.transform.VariableMapSetter;
import de.featjar.base.io.format.ParseException;
import de.featjar.base.task.Executor;
import de.featjar.base.tree.Trees;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses feature model CNF formulas from FeatureIDE XML files. Returns a
 * formula that is already partially in CNF, except for cross-tree constraints.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class XMLFeatureModelCNFFormat extends XMLFeatureModelFormat {
    @Override
    public XMLFeatureModelCNFFormat getInstance() {
        return new XMLFeatureModelCNFFormat();
    }

    @Override
    public String getName() {
        return "FeatureIDECNF";
    }

    @Override
    protected Formula parseDocument(Document document) throws ParseException {
        final Element featureModelElement = getDocumentElement(document, FEATURE_MODEL);
        parseFeatureTree(getElement(featureModelElement, STRUCT));
        Optional<Element> constraintsElement = getOptionalElement(featureModelElement, CONSTRAINTS);
        if (constraintsElement.isPresent()) {
            parseConstraints(constraintsElement.get(), variableMap);
        }
        return Trees.clone(simplify(new And(constraints)));
    }

    @Override
    protected void addConstraint(Boolean constraintLabel, Formula formula) throws ParseException {
        Formula transformedFormula = Executor.apply(new CNFTransformer(), formula)
                .orElseThrow(p -> new ParseException("failed to transform " + formula));
        transformedFormula = Formulas.manipulate(transformedFormula, new VariableMapSetter(variableMap)); // todo: this
        // is a
        // workaround
        // for weird
        // variableMap
        // shenanigans
        super.addConstraint(constraintLabel, transformedFormula);
    }

    @Override
    protected Formula atMostOne(List<? extends Formula> parseFeatures) {
        return new And(groupElements(
                parseFeatures.stream().map(Not::new).collect(Collectors.toList()), 1, parseFeatures.size()));
    }

    @Override
    protected Formula biImplies(Formula a, Formula b) {
        return new And(new Or(new Not(a), b), new Or(new Not(b), a));
    }

    @Override
    protected Formula implies(Literal a, Formula b) {
        return new Or(a.flip(), b);
    }

    @Override
    protected Formula implies(Formula a, Formula b) {
        return new Or(new Not(a), b);
    }

    @Override
    protected Formula implies(Literal f, List<? extends Formula> parseFeatures) {
        final ArrayList<Formula> list = new ArrayList<>(parseFeatures.size() + 1);
        list.add(f.flip());
        list.addAll(parseFeatures);
        return new Or(list);
    }

    private List<Formula> groupElements(List<? extends Formula> elements, int k, final int n) {
        final List<Formula> groupedElements = new ArrayList<>();
        final Formula[] clause = new Formula[k + 1];
        final int[] index = new int[k + 1];

        // the position that is currently filled in clause
        int level = 0;
        index[level] = -1;

        while (level >= 0) {
            // fill this level with the next element
            index[level]++;
            // did we reach the maximum for this level
            if (index[level] >= (n - (k - level))) {
                // go to previous level
                level--;
            } else {
                clause[level] = elements.get(index[level]);
                if (level == k) {
                    final Formula[] clonedClause = new Formula[clause.length];
                    Arrays.copyOf(clause, clause.length);
                    for (int i = 0; i < clause.length; i++) {
                        clonedClause[i] = clause[i];
                    }
                    groupedElements.add(new Or(clonedClause));
                } else {
                    // go to next level
                    level++;
                    // allow only ascending orders (to prevent from duplicates)
                    index[level] = index[level - 1];
                }
            }
        }
        return groupedElements;
    }

    private static Formula simplify(Formula formula) {
        final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
        Trees.traverse(auxiliaryRoot, new DeMorganVisitor());
        Trees.traverse(auxiliaryRoot, new TreeSimplifier());
        return auxiliaryRoot.getChild();
    }
}
