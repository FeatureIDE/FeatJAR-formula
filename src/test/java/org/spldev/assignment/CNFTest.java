/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.util.tree.*;

public class CNFTest {

	@Test
	public void convert() {
		final VariableMap variables = new VariableMap(Arrays.asList("a", "b", "c"));
		final Literal a = variables.createLiteral("a");
		final Literal b = variables.createLiteral("b");
		final Literal c = variables.createLiteral("c");

		final Implies implies1 = new Implies(a, b);
		final Or or = new Or(implies1, c);
		final Biimplies equals = new Biimplies(a, b);
		final And and = new And(equals, c);
		final Implies formula = new Implies(or, and);

		final Formula cnfFormula = Formulas.toCNF(formula).get();

		final Or or2 = new Or(a, c);
		final Or or3 = new Or(a, b.flip());
		final Or or4 = new Or(c, b.flip());
		final Or or5 = new Or(b, a.flip(), c.flip());
		final And and2 = new And(or2, or3, or4, or5);

		sortChildren(cnfFormula);
		sortChildren(and2);
		assertEquals(Trees.getPreOrderList(cnfFormula), Trees.getPreOrderList(and2));
		assertEquals(Trees.getPostOrderList(cnfFormula), Trees.getPostOrderList(and2));
	}

	private void sortChildren(final Formula root) {
		Trees.postOrderStream(root).forEach(node -> {
			final ArrayList<Formula> sortedChildren = new ArrayList<>(node.getChildren());
			Collections.sort(sortedChildren, Comparator.comparing(e -> Trees.getPreOrderList(e).toString()));
			node.setChildren(sortedChildren);
		});
	}

}
