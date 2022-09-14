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
package de.featjar.structure;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.featjar.formula.structure.Expression;
import de.featjar.formula.tmp.Formulas;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.tmp.TermMap;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Implies;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class NonTerminalExpressionTest {
    @Test
    public void createSimpleFormulas() {
        final TermMap map = new TermMap();
        final Literal p1 = map.createLiteral("p");
        final Literal p2 = map.createLiteral("p");
        assertEquals(p1, p2);
        assertNotSame(p1, p2);
        final Literal q = map.createLiteral("q");
        assertDoesNotThrow(() -> new And(p1, q));
        assertEquals(new And(p1, q), new And(p2, q));
        assertNotEquals(new And(p1, q), new And(q, p2));
        assertDoesNotThrow(() -> Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q"))));
        assertEquals(
                (Literal) Formulas.create(m -> m.createLiteral("1")),
                Formulas.create(m -> m.createLiteral("1")));
        assertEquals(
                (Literal) Formulas.create(m -> m.createLiteral("1", false)),
                Formulas.create(m -> m.createLiteral("1", false)));
        assertNotEquals(
                (Literal) Formulas.create(m -> m.createLiteral("1", false)),
                Formulas.create(m -> m.createLiteral("1")));
    }

    @Test
    public void ensureSharedVariableMap() {
        final TermMap map1 = new TermMap();
        final TermMap map2 = new TermMap();
        assertDoesNotThrow(() -> new And(Expression.TRUE, Expression.FALSE));
        assertDoesNotThrow(() -> new And(map1.createLiteral("a"), map1.createLiteral("a", false)));
        assertThrows(
                IllegalArgumentException.class, () -> new And(map1.createLiteral("a"), map2.createLiteral("a", false)));
        Expression expression1 = Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q")));
        Expression expression2 = Formulas.create(m -> new Implies(m.createLiteral("q"), m.createLiteral("r")));
        assertThrows(IllegalArgumentException.class, () -> new And(expression1, expression2));
    }

    @Test
    public void compose() {
        {
            Expression expression1 = Formulas.create(m -> m.createLiteral("a"));
            Expression expression2 = Formulas.create(m -> m.createLiteral("a"));
            assertThrows(IllegalArgumentException.class, () -> new And(expression1, expression2));
            assertDoesNotThrow(() -> Formulas.compose(And::new, expression1, expression2));
            Expression expression3 = Formulas.compose(And::new, expression1, expression2);
            Literal x = expression3.getFirstChild()
                    .get()
                    .getTermMap()
                    .orElseThrow()
                    .createLiteral("x");
            assertDoesNotThrow(() -> Formulas.compose(And::new, expression3, x));
        }
        Consumer<Expression> test = formula1 -> {
            Expression expression2 = Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q")));
            Expression expression3 = Formulas.create(m -> new Implies(m.createLiteral("q"), m.createLiteral("r")));
            assertThrows(IllegalArgumentException.class, () -> new And(formula1, expression2, expression3));
            assertDoesNotThrow(() -> Formulas.compose(And::new, formula1, expression2, expression3));
            Expression expression = Formulas.compose(And::new, formula1, expression2, expression3);
            assertNotEquals(formula1.getTermMap(), expression);
            assertNotEquals(expression2.getTermMap(), expression);
            assertNotEquals(expression3.getTermMap(), expression);
            assertEquals(Optional.empty(), expression.getChildren().get(0).getTermMap());
            assertEquals(expression.getTermMap(), expression.getChildren().get(1).getTermMap());
            assertEquals(expression.getTermMap(), expression.getChildren().get(2).getTermMap());
        };
        TermMap m = new TermMap();
        List.of(m.createLiteral("p"), m.createLiteral("q"));
        test.accept(new And());
        test.accept(Expression.TRUE);
    }
}
