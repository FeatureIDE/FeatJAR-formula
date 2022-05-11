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

import java.util.*;
import java.util.function.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.structure.term.bool.*;

public class FormulaCreator {

	public static Formula getFormula01() {
		final VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		return new Implies(new And(new Or(p, q), r), s.flip());
	}

	public static Formula getFormula02() {
		final VariableMap map = VariableMap.fromNames(Arrays.asList("p", "q", "r", "s"));
		final Literal p = new LiteralPredicate((BoolVariable) map.getVariable("p").get(), true);
		final Literal q = new LiteralPredicate((BoolVariable) map.getVariable("q").get(), true);
		final Literal r = new LiteralPredicate((BoolVariable) map.getVariable("r").get(), true);
		final Literal s = new LiteralPredicate((BoolVariable) map.getVariable("s").get(), true);

		return new And(
			new Implies(
				r,
				new And(p, q)),
			new Implies(
				s,
				new And(q, p)),
			new Or(
				new And(s.flip(), r),
				new And(s, r.flip())));
	}

	public static void testAllAssignments(VariableMap map, Consumer<Assignment> testFunction) {
		final Assignment assignment = new VariableAssignment(map);
		final int numVariables = map.size();
		final int numAssignments = (int) Math.pow(2, numVariables);
		for (int i = 0; i < numAssignments; i++) {
			for (int j = 0; j < numVariables; j++) {
				assignment.set(j + 1, ((i >> j) & 1) == 1);
			}
			testFunction.accept(assignment);
		}
	}

}
