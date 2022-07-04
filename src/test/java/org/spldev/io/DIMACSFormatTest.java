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
import org.spldev.formula.io.dimacs.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;

/**
 * Tests {@link DIMACSFormat DIMACS} format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSFormatTest {

	@Test
	public void DIMACS_123_n1n2n3() {
		test("123-n1n2n3");
	}

	@Test
	public void DIMACS_ABC_nAnBnC() {
		test("ABC-nAnBnC");
	}

	@Test
	public void DIMACS_empty() {
		test("empty");
	}

	@Test
	public void DIMACS_empty_1() {
		test("empty-1");
	}

	@Test
	public void DIMACS_empty_A() {
		test("empty-A");
	}

	@Test
	public void DIMACS_empty_ABC() {
		test("empty-ABC");
	}

	@Test
	public void DIMACS_empty_A2C() {
		test("empty-A2C");
	}

	@Test
	public void DIMACS_nA() {
		test("nA");
	}

	@Test
	public void DIMACS_nAB() {
		test("nAB");
	}

	@Test
	public void DIMACS_faulty() {
		test("faulty");
	}

	@Test
	public void DIMACS_void() {
		test("void");
	}

	private static void test(String name) {
		testLoadAndSave(getFormula(name), name, new DIMACSFormat());
	}

	private static Formula getFormula(String name) {
		switch (name) {
		case "faulty": {
			return null;
		}
		case "void": {
			return new And(new Or());
		}
		case "123-n1n2n3": {
			final VariableMap map = new VariableMap();
			final Literal a = map.createLiteral("1");
			final Literal b = map.createLiteral("2");
			final Literal c = map.createLiteral("3");
			return new And(
				new Or(a.cloneNode(), b.cloneNode(), c.cloneNode()),
				new Or(a.flip(), b.flip(), c.flip()));
		}
		case "ABC-nAnBnC": {
			final VariableMap map = new VariableMap();
			final Literal a = map.createLiteral("A");
			final Literal b = map.createLiteral("B");
			final Literal c = map.createLiteral("C");
			return new And(
				new Or(a.cloneNode(), b.cloneNode(), c.cloneNode()),
				new Or(a.flip(), b.flip(), c.flip()));
		}
		case "empty-ABC": {
			final VariableMap map = new VariableMap();
			map.addBooleanVariable("A");
			map.addBooleanVariable("B");
			map.addBooleanVariable("C");
			return new And();
		}
		case "empty-A2C": {
			final VariableMap map = new VariableMap();
			map.addBooleanVariable("A");
			map.addBooleanVariable("2");
			map.addBooleanVariable("C");
			return new And();
		}
		case "empty-A": {
			final VariableMap map = new VariableMap();
			map.addBooleanVariable("A");
			return new And();
		}
		case "empty-1": {
			final VariableMap map = new VariableMap();
			map.addBooleanVariable("1");
			return new And();
		}
		case "empty": {
			return new And();
		}
		case "nA": {
			final VariableMap map = new VariableMap();
			final Literal a = new BooleanLiteral(map.addBooleanVariable("A"));
			return new And(new Or(a.flip()));
		}
		case "nAB": {
			final VariableMap map = new VariableMap("A", "B");
			final Literal a = map.createLiteral("A");
			final Literal b = map.createLiteral("B");
			return new And(new Or(a.flip(), b.cloneNode()));
		}
		default:
			fail(name);
			return null;
		}
	}

}
