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
package de.featjar.formula.structure.transform;

import java.util.*;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.*;
import de.featjar.formula.structure.atomic.*;
import de.featjar.formula.structure.compound.*;
import de.featjar.util.tree.visitor.*;

public class DNFTester extends NFTester {

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Formula node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Or) {
			if (path.size() > 1) {
				isNf = false;
				isClausalNf = false;
				return VisitorResult.SkipAll;
			}
			for (final Formula child : node.getChildren()) {
				if (!(child instanceof And)) {
					if (!(child instanceof Atomic)) {
						isNf = false;
						isClausalNf = false;
						return VisitorResult.SkipAll;
					}
					isClausalNf = false;
				}
			}
			return VisitorResult.Continue;
		} else if (node instanceof And) {
			if (path.size() > 2) {
				isNf = false;
				isClausalNf = false;
				return VisitorResult.SkipAll;
			}
			if (path.size() < 2) {
				isClausalNf = false;
			}
			for (final Formula child : node.getChildren()) {
				if (!(child instanceof Atomic)) {
					isNf = false;
					isClausalNf = false;
					return VisitorResult.SkipAll;
				}
			}
			return VisitorResult.Continue;
		} else if (node instanceof Atomic) {
			if (path.size() > 3) {
				isNf = false;
				isClausalNf = false;
				return VisitorResult.SkipAll;
			}
			if (path.size() < 3) {
				isClausalNf = false;
			}
			return VisitorResult.SkipChildren;
		} else {
			isNf = false;
			isClausalNf = false;
			return VisitorResult.SkipAll;
		}
	}

}
