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

public class CNFTest {

    //    @Test
    //    public void convert() {
    //        final TermMap variables = new TermMap(Arrays.asList("a", "b", "c"));
    //        final Literal a = variables.createLiteral("a");
    //        final Literal b = variables.createLiteral("b");
    //        final Literal c = variables.createLiteral("c");
    //
    //        final Implies implies1 = new Implies(a, b);
    //        final Or or = new Or(implies1, c);
    //        final BiImplies equals = new BiImplies(a, b);
    //        final And and = new And(equals, c);
    //        final Implies formula = new Implies(or, and);
    //
    //        final Expression cnfExpression = Formulas.toCNF(formula).get();
    //
    //        final Or or2 = new Or(a, c);
    //        final Or or3 = new Or(a, b.invert());
    //        final Or or4 = new Or(c, b.invert());
    //        final Or or5 = new Or(b, a.invert(), c.invert());
    //        final And and2 = new And(or2, or3, or4, or5);
    //
    //        sortChildren(cnfExpression);
    //        sortChildren(and2);
    //        assertEquals(cnfExpression.getDescendantsAsPreOrder(), and2.getDescendantsAsPreOrder());
    //        assertEquals(cnfExpression.getDescendantsAsPostOrder(), and2.getDescendantsAsPostOrder());
    //    }
    //
    //    private void sortChildren(final Expression root) {
    //        Trees.postOrderStream(root).forEach(node -> {
    //            final ArrayList<Expression> sortedChildren = new ArrayList<>(node.getChildren());
    //            Collections.sort(sortedChildren, Comparator.comparing(e -> e.getDescendantsAsPreOrder()
    //                    .toString()));
    //            node.setChildren(sortedChildren);
    //        });
    //    }
}
