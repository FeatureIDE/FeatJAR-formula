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
package org.spldev.clauses.solutions.analysis;

import java.util.function.*;

import org.spldev.clauses.*;

public class InteractionFinderCombination implements InteractionFinder {

	private final InteractionFinder finder;
	private final Predicate<LiteralList> verifyer;

	public InteractionFinderCombination(InteractionFinder finder, Predicate<LiteralList> verifyer) {
		this.finder = finder;
		this.verifyer = verifyer;
	}

	public LiteralList find(int maxT) {
		LiteralList[] results = new LiteralList[maxT];
		for (int t = 1; t <= maxT; t++) {
			results[t - 1] = finder.find(t);
		}
		LiteralList lastResult = null;
		for (int t = maxT; t >= 1; t--) {
			final LiteralList result = results[t - 1];
			if (!result.isEmpty()) {
				if (lastResult == null) {
					lastResult = result;
				} else {
					if (lastResult.containsAll(result)) {
						final LiteralList test = result.addAll(lastResult.removeAll(result).negate());
						if (verifyer.test(test)) {
							LiteralList newResult = finder.find(lastResult.size());
							return newResult.isEmpty() ? lastResult : newResult;
						} else {
							LiteralList newResult = finder.find(result.size());
							return newResult.isEmpty() ? result : newResult;
						}
					}
				}
			}
		}
		return lastResult == null ? new LiteralList() : lastResult;
	}

}
