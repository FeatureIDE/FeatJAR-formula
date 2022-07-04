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
package org.spldev.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.spldev.io.FormatTest.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.io.textual.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;

/**
 * Tests {@link FormulaFormat Formula} format.
 *
 * @author Sebastian Krieter
 */
public class FormulaFormatTest {

	@Test
	public void Formula_ABC_nAnBnC() {
		test("ABC-nAnBnC");
	}

	@Test
	public void Formula_empty() {
		test("faulty");
	}

	@Test
	public void Formula_nA() {
		test("nA");
	}

	@Test
	public void Formula_nAB() {
		test("nAB");
	}

	private static void test(String name) {
		testLoadAndSave(getFormula(name), name, new FormulaFormat());
	}

	private static Formula getFormula(String name) {
		switch (name) {
		case "faulty": {
			return null;
		}
		case "ABC-nAnBnC": {
			final VariableMap map = new VariableMap();
			final Literal a = map.createLiteral("A");
			final Literal b = map.createLiteral("B");
			final Literal c = map.createLiteral("C");
			return new And(
				new Or(a.cloneNode(), new Or(b.cloneNode(), c.cloneNode())),
				new Or(new Not(a.cloneNode()), new Or(new Not(b.cloneNode()), new Not(c.cloneNode()))));
		}
		case "nA": {
			final VariableMap map = new VariableMap();
			final Literal a = map.createLiteral("A");
			return new Not(a.cloneNode());
		}
		case "nAB": {
			final VariableMap map = new VariableMap();
			final Literal a = map.createLiteral("A");
			final Literal b = map.createLiteral("B");
			return new Or(new Not(a.cloneNode()), b.cloneNode());
		}
		default:
			fail(name);
			return null;
		}
	}

}
