/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.structure.transform;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.atomic.literal.VariableMap.*;
import org.spldev.util.tree.visitor.*;

public class VariableMapSetter implements TreeVisitor<Void, Formula> {

	private final VariableMap variableMap;

	public VariableMapSetter(VariableMap variableMap) {
		this.variableMap = variableMap;
	}

	private Formula replaceValueTerms(Formula node) {
		if (node instanceof Variable) {
			final Variable replacement = variableMap.getVariable(node.getName()).orElseThrow(
				() -> new IllegalArgumentException(
					"Map does not contain variable with name " + node.getName()));
			return replacement;
		} else if (node instanceof Constant) {
			final Constant replacement = variableMap.getConstant(node.getName()).orElseThrow(
				() -> new IllegalArgumentException(
					"Map does not contain constant with name " + node.getName()));
			return replacement;
		}
		return node;
	}

	@Override
	public VisitorResult lastVisit(List<Formula> path) {
		final Formula node = TreeVisitor.getCurrentNode(path);
		node.mapChildren(this::replaceValueTerms);
		return TreeVisitor.super.lastVisit(path);
	}

}
