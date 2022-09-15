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
package de.featjar.formula.visitor;

import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.transformer.Transformer;
import de.featjar.formula.tmp.AuxiliaryRoot;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.data.Result;
import de.featjar.base.tree.Trees;

/**
 * Transforms propositional formulas into (clausal) CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public class NormalForms {

    private NormalForms() {}

    public enum NormalForm {
        CNF,
        DNF
        // todo: NNF tester
    }

    protected static NormalFormTester getNormalFormTester(Formula formula, NormalForm normalForm) {
        NormalFormTester tester;
        switch (normalForm) {
            case CNF:
                tester = new CNFTester();
                break;
            case DNF:
                tester = new DNFTester();
                break;
            default:
                throw new IllegalStateException(String.valueOf(normalForm));
        }
        formula.traverse(tester);
        return tester;
    }

    public static boolean isNormalForm(Formula formula, NormalForm normalForm, boolean clausal) {
        final NormalFormTester tester = getNormalFormTester(formula, normalForm);
        return clausal ? tester.isClausalNormalForm() : tester.isNormalForm();
    }

    public static Expression simplifyForNF(Expression expression) {
        final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(expression);
        Trees.traverse(auxiliaryRoot, new ConnectiveSimplifier());
        Trees.traverse(auxiliaryRoot, new DeMorganApplier());
        Trees.traverse(auxiliaryRoot, new AndOrSimplifier());
        return auxiliaryRoot.getChild();
    }

    public static Result<Expression> toNF(Expression root, Transformer transformer) {
        return transformer.apply(root);
    }

    public static Expression toClausalNF(Expression expression, NormalForm normalForm) {
        switch (normalForm) {
            case CNF:
                if (expression instanceof Literal) {
                    expression = new And(new Or(expression));
                } else if (expression instanceof Or) {
                    expression = new And(expression);
                } else {
                    expression.replaceChildren(child -> (child instanceof Literal ? new Or(child) : child));
                }
                break;
            case DNF:
                if (expression instanceof Literal) {
                    expression = new Or(new And(expression));
                } else if (expression instanceof And) {
                    expression = new Or(new And(expression));
                } else {
                    expression.replaceChildren(child -> (child instanceof Literal ? new And(child) : child));
                }
                break;
            default:
        }
        return expression;
    }
}
