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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.featjar.clauses.LiteralList;

public class InteractionFinderSplit2 extends AbstractInteractionFinder {

	public InteractionFinderSplit2(Collection<LiteralList> sample,
		SolutionUpdater configurationGenerator,
		Predicate<LiteralList> configurationChecker) {
		super(sample, configurationGenerator, configurationChecker);
	}

	public List<LiteralList> find(int t, int n) {
		return Arrays.asList(binarySearch(computePotentialInteractions(t)));
	}

	private LiteralList binarySearch(List<LiteralList> possibleInteractions) {
		List<LiteralList> interactionsAll = possibleInteractions;
		while (interactionsAll.size() > 1) {
			final List<LiteralList> interactionsLeft = new ArrayList<>(interactionsAll.size());
			final List<LiteralList> interactionsRight = new ArrayList<>(interactionsAll.size());

			split(interactionsAll, interactionsLeft, interactionsRight);

			final List<LiteralList> configs = getConfigurations(interactionsLeft, interactionsRight);
			if (configs == null) {
				return new LiteralList();
			}
			LiteralList c1 = configs.get(0);
			LiteralList c2 = configs.get(1);

//			LiteralList c1 = getConfiguration(interactionsLeft);
			if (!verifier.test(c1)) {
				failingConfs.add(c1);
				// TODO generate configuration that explicitly forbids interaction of left half
//				LiteralList c2 = getConfiguration(interactionsRight);
				if (!verifier.test(c2)) {
					failingConfs.add(c2);
//					int oldSize = interactionsAll.size();
					interactionsAll = interactionsAll.stream() //
						.filter(interaction -> c1.containsAll(interaction) && c2.containsAll(interaction)) //
						.collect(Collectors.toList());
					// TODO can fix potential endless loop, but leads to bad results
//					if (interactionsAll.size() == oldSize) {
//						return new LiteralList();
//					}
				} else {
					validConfs.add(c2);
					interactionsAll = interactionsLeft;
				}
			} else {
				validConfs.add(c1);
				interactionsAll = interactionsRight;
			}
		}
		return !interactionsAll.isEmpty() //
			? interactionsAll.iterator().next() //
			: new LiteralList();
	}

	private void split(final List<LiteralList> interactionsAll,
		final List<LiteralList> interactionsLeft, final List<LiteralList> interactionsRight) {
		Collections.sort(interactionsAll, Comparator.comparing(InteractionFinderSplit2::maxVariable));
		int middleIndex = interactionsAll.size() / 2;
		ListIterator<LiteralList> it = interactionsAll.listIterator(middleIndex - 1);
		final LiteralList middle = it.next();
		final int middleMax = middle.get(middle.size() - 1);
		while (it.hasNext()) {
			final LiteralList next = it.next();
			if (middleMax != next.get(next.size() - 1)) {
				break;
			}
		}
		final int splitIndex = it.previousIndex();
		interactionsLeft.addAll(interactionsAll.subList(0, splitIndex));
		interactionsRight.addAll(interactionsAll.subList(splitIndex, interactionsAll.size()));
	}

}
