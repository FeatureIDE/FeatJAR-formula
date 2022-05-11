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

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.spldev.clauses.*;

/**
 * Detect interactions from given set of configurations.
 * 
 * @author Jens Meinicke
 * @author Sebastian Krieter
 *
 *         TODO how to detect A || B?
 */
public class InteractionFinderSplit extends AbstractInteractionFinder {

	public InteractionFinderSplit(Collection<LiteralList> sample,
		SolutionUpdater configurationGenerator,
		Predicate<LiteralList> configurationChecker) {
		super(sample, configurationGenerator, configurationChecker);
	}

	public LiteralList find(int t) {
		return binarySearch(computePotentialInterations(t));
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
			if (!verifyer.test(c1)) {
				failingConfs.add(c1);
				// TODO generate configuration that explicitly forbids interaction of left half
//				LiteralList c2 = getConfiguration(interactionsRight);
				if (!verifyer.test(c2)) {
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
		final List<Integer> literals = interactionsAll
			.stream()
			.flatMapToInt(l -> Arrays.stream(l.getLiterals()))
			.distinct().boxed().collect(Collectors.toList());
		LinkedHashSet<Integer> selections = new LinkedHashSet<>(literals.subList(0, (int) (literals.size() * 0.7)));
		interactionsAll.stream().forEach(interaction -> {
			if (IntStream.of(interaction.getLiterals()).allMatch(selections::contains)) {
				interactionsLeft.add(interaction);
			} else {
				interactionsRight.add(interaction);
			}
		});
		if (interactionsLeft.isEmpty() || interactionsRight.isEmpty()) {
			selections.clear();
			interactionsLeft.clear();
			interactionsRight.clear();
			IntStream.of(interactionsAll.get(0).getLiterals()).forEach(selections::add);
			interactionsAll.stream().forEach(interaction -> {
				if (IntStream.of(interaction.getLiterals()).allMatch(selections::contains)) {
					interactionsLeft.add(interaction);
				} else {
					interactionsRight.add(interaction);
				}
			});
		}
	}

}
