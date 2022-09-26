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
package de.featjar.clauses.solutions.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import de.featjar.clauses.LiteralList;

public class InteractionFinderCombination implements InteractionFinder {

	private final InteractionFinder finder;
	private final Predicate<LiteralList> verifyer;

	public InteractionFinderCombination(InteractionFinder finder, Predicate<LiteralList> verifyer) {
		this.finder = finder;
		this.verifyer = verifyer;
	}

	// TODO adjust for multiple interactions found for a given t
	public List<LiteralList> find(int maxT, int n) {
		List<LiteralList> results = new ArrayList<>(maxT);
		for (int t = 1; t <= maxT; t++) {
			results.addAll(finder.find(t, n));
		}
		LiteralList lastResult = null;
		for (int i = results.size(); i >= 0; i--) {
			final LiteralList result = results.get(i);
			if (!result.isEmpty()) {
				if (lastResult == null) {
					lastResult = result;
				} else {
					if (lastResult.containsAll(result)) {
						final LiteralList test = result.addAll(lastResult.removeAll(result).negate());
						if (verifyer.test(test)) {
							List<LiteralList> newResult = finder.find(lastResult.size(), n);
							return newResult.isEmpty() ? Arrays.asList(lastResult) : newResult;
						} else {
							List<LiteralList> newResult = finder.find(result.size(), n);
							return newResult.isEmpty() ? Arrays.asList(result) : newResult;
						}
					}
				}
			}
		}
		return lastResult == null ? new ArrayList<>() : Arrays.asList(lastResult);
	}

}
