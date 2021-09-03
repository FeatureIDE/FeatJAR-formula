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
package org.spldev.formula.expression.transform;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.util.tree.visitor.*;

public class DNFTester extends NFTester {

	@Override
	public VisitorResult firstVisit(List<Expression> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Or) {
			if (path.size() > 1) {
				isNf = false;
				isClausalNf = false;
				return VisitorResult.SkipAll;
			}
			for (final Expression child : node.getChildren()) {
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
			for (final Expression child : node.getChildren()) {
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
