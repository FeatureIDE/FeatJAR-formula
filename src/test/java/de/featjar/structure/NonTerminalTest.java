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

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.literal.BooleanLiteral;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Implies;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import de.featjar.util.tree.Trees;
import org.junit.jupiter.api.Test;

public class NonTerminalTest {
    @Test
    public void createSimpleFormulas() {
        final VariableMap map = new VariableMap();
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
                (BooleanLiteral) Formulas.create(m -> m.createLiteral("1")),
                Formulas.create(m -> m.createLiteral("1")));
        assertEquals(
                (BooleanLiteral) Formulas.create(m -> m.createLiteral("1", false)),
                Formulas.create(m -> m.createLiteral("1", false)));
        assertNotEquals(
                (BooleanLiteral) Formulas.create(m -> m.createLiteral("1", false)),
                Formulas.create(m -> m.createLiteral("1")));
    }

    @Test
    public void ensureSharedVariableMap() {
        final VariableMap map1 = new VariableMap();
        final VariableMap map2 = new VariableMap();
        assertDoesNotThrow(() -> new And(Literal.True, Literal.False));
        assertDoesNotThrow(() -> new And(map1.createLiteral("a"), map1.createLiteral("a", false)));
        assertThrows(
                IllegalArgumentException.class, () -> new And(map1.createLiteral("a"), map2.createLiteral("a", false)));
        Formula formula1 = Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q")));
        Formula formula2 = Formulas.create(m -> new Implies(m.createLiteral("q"), m.createLiteral("r")));
        assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2));
    }

    @Test
    public void compose() {
        {
            Formula formula1 = Formulas.create(m -> m.createLiteral("a"));
            Formula formula2 = Formulas.create(m -> m.createLiteral("a"));
            assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2));
            assertDoesNotThrow(() -> Formulas.compose(And::new, formula1, formula2));
            Formula formula3 = Formulas.compose(And::new, formula1, formula2);
            Literal x = formula3.getFirstChild()
                    .get()
                    .getVariableMap()
                    .orElseThrow()
                    .createLiteral("x");
            assertDoesNotThrow(() -> Formulas.compose(And::new, formula3, x));
        }
        Consumer<Formula> test = formula1 -> {
            Formula formula2 = Formulas.create(m -> new Implies(m.createLiteral("p"), m.createLiteral("q")));
            Formula formula3 = Formulas.create(m -> new Implies(m.createLiteral("q"), m.createLiteral("r")));
            assertThrows(IllegalArgumentException.class, () -> new And(formula1, formula2, formula3));
            assertDoesNotThrow(() -> Formulas.compose(And::new, formula1, formula2, formula3));
            Formula formula = Formulas.compose(And::new, formula1, formula2, formula3);
            assertNotEquals(formula1.getVariableMap(), formula);
            assertNotEquals(formula2.getVariableMap(), formula);
            assertNotEquals(formula3.getVariableMap(), formula);
            assertEquals(Optional.empty(), formula.getChildren().get(0).getVariableMap());
            assertEquals(formula.getVariableMap(), formula.getChildren().get(1).getVariableMap());
            assertEquals(formula.getVariableMap(), formula.getChildren().get(2).getVariableMap());
        };
        VariableMap m = new VariableMap();
        List.of(m.createLiteral("p"), m.createLiteral("q"));
        test.accept(new And());
        test.accept(Literal.True);
    }
}
