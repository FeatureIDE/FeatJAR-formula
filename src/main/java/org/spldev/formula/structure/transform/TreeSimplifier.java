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
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.util.tree.visitor.*;

public class TreeSimplifier implements TreeVisitor<Void, Formula> {

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Formula node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VisitorResult.SkipChildren;
		} else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
			return VisitorResult.Continue;
		} else {
			return VisitorResult.Fail;
		}
	}

	@Override
	public VisitorResult lastVisit(List<Formula> path) {
		final Formula node = TreeVisitor.getCurrentNode(path);
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

	private List<? extends Formula> mergeAnd(final Formula child) {
		return (child instanceof And) || (!(child instanceof Atomic) && (child.getChildren().size() == 1)) ? child
			.getChildren() : null;
	}

	private List<? extends Formula> mergeOr(final Formula child) {
		return (child instanceof Or) || (!(child instanceof Atomic) && (child.getChildren().size() == 1)) ? child
			.getChildren() : null;
	}

}
