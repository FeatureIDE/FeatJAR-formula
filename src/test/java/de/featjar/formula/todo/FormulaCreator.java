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
package de.featjar.formula.todo;

public class FormulaCreator {

    //    public static Expression getFormula01() {
    //        final TermMap map = new TermMap(Arrays.asList("p", "q", "r", "s"));
    //        final Literal p = map.createLiteral("p");
    //        final Literal q = map.createLiteral("q");
    //        final Literal r = map.createLiteral("r");
    //        final Literal s = map.createLiteral("s");
    //
    //        return new Implies(new And(new Or(p, q), r), s.invert());
    //    }
    //
    //    public static Expression getFormula02() {
    //        final TermMap map = new TermMap(Arrays.asList("p", "q", "r", "s"));
    //        final Literal p = map.createLiteral("p");
    //        final Literal q = map.createLiteral("q");
    //        final Literal r = map.createLiteral("r");
    //        final Literal s = map.createLiteral("s");
    //
    //        return new And(
    //                new Implies(r, new And(p, q)),
    //                new Implies(s, new And(q, p)),
    //                new Or(new And(s.invert(), r), new And(s, r.invert())));
    //    }
    //
    //    public static void testAllAssignments(TermMap map, Consumer<Assignment> testFunction) {
    //        final Assignment assignment = new NameAssignment(map);
    //        final int numVariables = map.getVariableCount();
    //        final int numAssignments = (int) Math.pow(2, numVariables);
    //        for (int i = 0; i < numAssignments; i++) {
    //            for (int j = 0; j < numVariables; j++) {
    //                assignment.set(j + 1, ((i >> j) & 1) == 1);
    //            }
    //            testFunction.accept(assignment);
    //        }
    //    }
}
