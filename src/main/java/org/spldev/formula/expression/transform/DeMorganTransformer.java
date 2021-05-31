/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.transform;

import java.util.List;
import java.util.stream.Collectors;

import org.spldev.formula.expression.AuxiliaryRoot;
import org.spldev.formula.expression.Expression;
import org.spldev.formula.expression.Formula;
import org.spldev.formula.expression.atomic.Atomic;
import org.spldev.formula.expression.atomic.literal.Literal;
import org.spldev.formula.expression.compound.And;
import org.spldev.formula.expression.compound.Compound;
import org.spldev.formula.expression.compound.Not;
import org.spldev.formula.expression.compound.Or;
import org.spldev.util.tree.visitor.TreeVisitor;

public class DeMorganTransformer implements TreeVisitor<Void, Expression> {

	@Override
	public VistorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VistorResult.SkipChildren;
		} else if (node instanceof Compound) {
			node.mapChildren(this::replace);
			return VistorResult.Continue;
		} else if (node instanceof AuxiliaryRoot) {
			node.mapChildren(this::replace);
			return VistorResult.Continue;
		} else {
			return VistorResult.Fail;
		}
	}

	private Expression replace(Expression node) {
		Expression newNode = node;
		while (newNode instanceof Not) {
			final Formula notChild = (Formula) newNode.getChildren().iterator().next();
			if (notChild instanceof Literal) {
				newNode = ((Literal) notChild).flip();
			} else if (notChild instanceof Not) {
				newNode = notChild.getChildren().get(0);
			} else if (notChild instanceof Or) {
				newNode = new And(((Compound) notChild).getChildren().stream().map(Not::new).collect(Collectors
					.toList()));
			} else if (notChild instanceof And) {
				newNode = new Or(((Compound) notChild).getChildren().stream().map(Not::new).collect(Collectors
					.toList()));
			}
		}
		return newNode;
	}

}
