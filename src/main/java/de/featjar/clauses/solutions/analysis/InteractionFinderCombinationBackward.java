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
import java.util.List;

import de.featjar.clauses.LiteralList;

public class InteractionFinderCombinationBackward implements InteractionFinder {

	private final InteractionFinder finder;

	public InteractionFinderCombinationBackward(InteractionFinder finder) {
		this.finder = finder;
	}

	public List<LiteralList> find(int maxT, int n) {
		List<List<LiteralList>> results = new ArrayList<>(maxT);
		for (int t = 1; t <= maxT; t++) {
			results.add(finder.find(t, n));
		}
		List<LiteralList> lastResult = null;
		for (int i = maxT - 1; i >= 0; i--) {
			final List<LiteralList> result = results.get(i);
			if (result.isEmpty()) {
				return lastResult == null ? new ArrayList<>() : lastResult;
			} else {
				if (lastResult == null) {
					lastResult = result;
				} else {

					LiteralList merge1 = update(merge(result));
					LiteralList merge2 = update(merge(lastResult));
					if (merge2.containsAll(merge1)) {
						if (!merge1.containsAll(merge2)) {
							LiteralList complete = complete(merge1, merge2.removeAll(merge1));
							if (complete != null && verify(complete)) {
								return lastResult;
							}
						}
						lastResult = result;
					} else {
						return lastResult;
					}
				}
			}
		}
		return lastResult == null ? new ArrayList<>() : lastResult;
	}

	@Override
	public boolean verify(LiteralList solution) {
		return finder.verify(solution);
	}

	@Override
	public void setCore(LiteralList coreDead) {
		finder.setCore(coreDead);
	}

	@Override
	public int getConfigurationCount() {
		return finder.getConfigurationCount();
	}

	@Override
	public List<?> getInteractionCounter() {
		return finder.getInteractionCounter();
	}

	@Override
	public LiteralList getCore() {
		return finder.getCore();
	}

	@Override
	public LiteralList merge(List<LiteralList> result) {
		return finder.merge(result);
	}

	@Override
	public LiteralList complete(LiteralList include, LiteralList... exclude) {
		return finder.complete(include, exclude);
	}

	@Override
	public LiteralList update(LiteralList result) {
		return finder.update(result);
	}

	@Override
	public int getConfigCreationCount() {
		return finder.getConfigCreationCount();
	}

	@Override
	public int getVerifyCount() {
		return finder.getVerifyCount();
	}

}
