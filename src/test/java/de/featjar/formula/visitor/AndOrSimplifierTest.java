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
package de.featjar.formula.visitor;

import static de.featjar.formula.structure.Expressions.*;

import org.junit.jupiter.api.Test;

class AndOrSimplifierTest {
    @Test
    void doesNothingForTautology() {
        VisitorTest.traverseAndAssertSameFormula(or(literal("x"), not(literal("x"))), new AndOrSimplifier());
    }

    @Test
    void nothingHappensForContradiction() {
        VisitorTest.traverseAndAssertSameFormula(and(literal("x"), not(literal("x"))), new AndOrSimplifier());
    }

    @Test
    void simplifiesUnaryOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), or(literal("x"))), new AndOrSimplifier(), and(literal("x"), literal("x")));
    }

    @Test
    void simplifiesUnaryAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), and(literal("x"))), new AndOrSimplifier(), or(literal("x"), literal("x")));
    }

    @Test
    void mergesAnd() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(literal("x"), and(literal("x"))), new AndOrSimplifier(), and(literal("x"), literal("x")));
    }

    @Test
    void mergesOr() {
        VisitorTest.traverseAndAssertFormulaEquals(
                or(literal("x"), or(literal("x"))), new AndOrSimplifier(), or(literal("x"), literal("x")));
    }

    @Test
    void simplifyComplex() {
        VisitorTest.traverseAndAssertFormulaEquals(
                and(
                        literal("a"),
                        and(literal("b"), literal("c"), True),
                        and(literal("b"), False),
                        or(literal("x"), False)),
                new AndOrSimplifier(),
                and(literal("a"), literal("b"), literal("c"), True, literal("b"), False, or(literal("x"), False)));
    }
}
