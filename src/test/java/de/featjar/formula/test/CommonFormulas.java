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
package de.featjar.formula.test;

import static de.featjar.formula.structure.Expressions.and;
import static de.featjar.formula.structure.Expressions.literal;
import static de.featjar.formula.structure.Expressions.not;
import static de.featjar.formula.structure.Expressions.or;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.formula.structure.formula.IFormula;

public class CommonFormulas {

    public static IFormula getFormula(String name) {
        switch (name) {
            case "faulty": {
                return null;
            }
            case "empty": {
                return and();
            }
            case "void": {
                return and(or());
            }
            case "123-n1n2n3": {
                return and(
                        or(literal("1"), literal("2"), literal("3")),
                        or(literal(false, "1"), literal(false, "2"), literal(false, "3")));
            }
            case "ABC-nAnBnC": {
                return and(
                        or(literal("A"), literal("B"), literal("C")),
                        or(not(literal("A")), or(not(literal("B")), not(literal("C")))));
            }
            case "nA": {
                return not(literal("A"));
            }
            case "nAB": {
                return or(not(literal("A")), literal("B"));
            }
            default:
                fail(name);
                return null;
        }
    }
}
