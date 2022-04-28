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
package org.spldev.formula.structure.transform;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.util.tree.visitor.*;

public class TreeSimplifier implements TreeVisitor<Void, Expression> {

	@Override
	public VisitorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VisitorResult.SkipChildren;
		} else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			return VisitorResult.Continue;
		} else {
			return VisitorResult.Fail;
		}
	}

	@Override
	public VisitorResult lastVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			if (node instanceof And) {
				if (node.getChildren().stream().anyMatch(c -> c == Literal.False)) {
					node.setChildren(Arrays.asList(Literal.False));
				} else {
					node.flatMapChildren(this::mergeAnd);
				}
			} else if (node instanceof Or) {
				if (node.getChildren().stream().anyMatch(c -> c == Literal.True)) {
					node.setChildren(Arrays.asList(Literal.True));
				} else {
					node.flatMapChildren(this::mergeOr);
				}
			}
		}
		return VisitorResult.Continue;
	}

	private List<? extends Expression> mergeAnd(final Expression child) {
		return (child instanceof And) || (!(child instanceof Atomic) && (child.getChildren().size() == 1)) ? child
			.getChildren() : null;
	}

	private List<? extends Expression> mergeOr(final Expression child) {
		return (child instanceof Or) || (!(child instanceof Atomic) && (child.getChildren().size() == 1)) ? child
			.getChildren() : null;
	}

}
