/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
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
package org.spldev.formula.expression.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.spldev.formula.expression.io.FormatTest.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;

/**
 * Tests {@link XmlFeatureModelFormat FeatureIDE} format.
 *
 * @author Sebastian Krieter
 */
public class XmlFeatureModelFormatTest {

	@Test
	public void FeatureIDE_ABC_nAnBnC() {
		test("ABC-nAnBnC");
	}

	@Test
	public void FeatureIDE_A() {
		test("A");
	}

	@Test
	public void FeatureIDE_CNF_SingleGroups() {
		test("SingleGroups");
	}

	@Test
	public void FeatureIDE_faulty() {
		test("faulty");
	}

	private static void test(String name) {
		testLoad(getFormula(name), name, new XmlFeatureModelFormat());
	}

	private static Formula getFormula(String name) {
		switch (name) {
		case "faulty": {
			return null;
		}
		case "ABC-nAnBnC": {
			final VariableMap map = VariableMap.emptyMap();
			final Literal root = new LiteralPredicate(map.addBooleanVariable("Root").get());
			final Literal a = new LiteralPredicate(map.addBooleanVariable("A").get());
			final Literal b = new LiteralPredicate(map.addBooleanVariable("B").get());
			final Literal c = new LiteralPredicate(map.addBooleanVariable("C").get());
			return new And(
				root.cloneNode(),
				new Implies(a.cloneNode(), root.cloneNode()),
				new Implies(b.cloneNode(), root.cloneNode()),
				new Implies(c.cloneNode(), root.cloneNode()),
				new Implies(root.cloneNode(), new Or(a.cloneNode(), b.cloneNode(), c.cloneNode())),
				new Or(new Not(a.cloneNode()), new Not(b.cloneNode()), new Not(c.cloneNode())));
		}
		case "SingleGroups": {
			final VariableMap map = VariableMap.emptyMap();
			final Literal root = new LiteralPredicate(map.addBooleanVariable("Root").get());
			final Literal a = new LiteralPredicate(map.addBooleanVariable("A").get());
			final Literal a1 = new LiteralPredicate(map.addBooleanVariable("A1").get());
			final Literal b = new LiteralPredicate(map.addBooleanVariable("B").get());
			final Literal b1 = new LiteralPredicate(map.addBooleanVariable("B1").get());
			return new And(
				root.cloneNode(),
				new Implies(a.cloneNode(), root.cloneNode()),
				new Implies(root.cloneNode(), a.cloneNode()),
				new Implies(a1.cloneNode(), a.cloneNode()),
				new Implies(a.cloneNode(), a1.cloneNode()),
				new Implies(b.cloneNode(), root.cloneNode()),
				new Implies(root.cloneNode(), b.cloneNode()),
				new Implies(b1.cloneNode(), b.cloneNode()),
				new Implies(b.cloneNode(), b1.cloneNode()));
		}
		case "A": {
			final VariableMap map = VariableMap.emptyMap();
			final Literal a = new LiteralPredicate(map.addBooleanVariable("A").get());
			return new And(a.cloneNode());
		}
		default:
			fail(name);
			return null;
		}
	}

}
