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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.featjar.clauses.LiteralList;

/**
 * Detect interactions from given set of configurations.
 * 
 * @author Jens Meinicke
 * @author Sebastian Krieter
 *
 *         TODO how to detect A || B?
 */
public class InteractionFinderSplit extends AbstractInteractionFinder {

	public InteractionFinderSplit(Collection<LiteralList> sample, SolutionUpdater configurationGenerator,
			Predicate<LiteralList> configurationChecker) {
		super(sample, configurationGenerator, configurationChecker);
	}

	public List<LiteralList> find(int t, int numberOfFeatures) {
		return binarySearch(computePotentialInteractions(t), numberOfFeatures);
	}

//	private List<LiteralList> binarySearch(List<LiteralList> possibleInteractions) {
//
//		List<LiteralList> interactionsAll = possibleInteractions;
//		System.out.println("All interactions: " + interactionsAll);
//		while (interactionsAll.size() > 1) {
//			final List<LiteralList> interactionsLeft = new ArrayList<>(interactionsAll.size());
//			final List<LiteralList> interactionsRight = new ArrayList<>(interactionsAll.size());
//
//			split(interactionsAll, interactionsLeft, interactionsRight);
//			System.out.println("LOOP_ start nach split: All interactions: " + interactionsAll);
//			System.out.println("Left interactions: " + interactionsLeft);
//			System.out.println("Right interactions: " + interactionsRight);
//
//			// HERE create config
//			final List<LiteralList> configs = getConfigurations(interactionsLeft, interactionsRight);
//			if (configs == null) {
//				continue;
//			}
//			LiteralList c1 = configs.get(0);
//			LiteralList c2 = configs.get(1);
//			System.out.println("1.config: " + c1);
//			System.out.println("2.config: " + c2);
//
////			LiteralList c1 = getConfiguration(interactionsLeft);
//			if (!verifier.test(c1)) {
//				failingConfs.add(c1);
//				// TODO generate configuration that explicitly forbids interaction of left half
////				LiteralList c2 = getConfiguration(interactionsRight);
//				if (!verifier.test(c2)) {
//					failingConfs.add(c2);
////					int oldSize = interactionsAll.size();
//					interactionsAll = interactionsAll.stream() //
//							.filter(interaction -> c1.containsAll(interaction) && c2.containsAll(interaction)) //
//							.collect(Collectors.toList());
//					// TODO can fix potential endless loop, but leads to bad results
////					if (interactionsAll.size() == oldSize) {
////						return new LiteralList();
////					}
//				} else {
//					validConfs.add(c2);
//					interactionsAll = interactionsAll.stream() //
//							.filter(interaction -> c1.containsAll(interaction) && !c2.containsAll(interaction)) //
//							.collect(Collectors.toList());
//				}
//			} else {
//				validConfs.add(c1);
//				interactionsAll = interactionsAll.stream() //
//						.filter(interaction -> !c1.containsAll(interaction) && c2.containsAll(interaction)) //
//						.collect(Collectors.toList());
//			}
//			System.out.println("LOOP_ ende mit interaction size: " + interactionsAll.size());
//		}
//		return !interactionsAll.isEmpty() //
//				? interactionsAll //
//				: new ArrayList<>();
//
//	}

	private void split(final List<LiteralList> interactionsAll, final List<LiteralList> interactionsLeft,
			final List<LiteralList> interactionsRight) {
		interactionsLeft.clear();
		interactionsRight.clear();
		final List<Integer> literals = interactionsAll.stream() //
				.flatMapToInt(l -> Arrays.stream(l.getLiterals())) //
				.distinct() //
				.boxed() //
				.collect(Collectors.toList());
		LinkedHashSet<Integer> selections = new LinkedHashSet<>(literals.subList(0, (int) (literals.size() * 0.7)));
		interactionsAll.stream().forEachOrdered(interaction -> {
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
			interactionsAll.stream().forEachOrdered(interaction -> {
				if (IntStream.of(interaction.getLiterals()).allMatch(selections::contains)) {
					interactionsLeft.add(interaction);
				} else {
					interactionsRight.add(interaction);
				}
			});
		}
	}

	private void split(final List<LiteralList> interactionsAll, final List<LiteralList> interactionsLeft,
			final List<LiteralList> interactionsRight, int k) {
		interactionsLeft.clear();
		interactionsRight.clear();
		LinkedHashSet<Integer> selections = new LinkedHashSet<>();
		IntStream.of(interactionsAll.get(k).getLiterals()).forEach(selections::add);
		interactionsAll.stream().forEachOrdered(interaction -> {
			if (IntStream.of(interaction.getLiterals()).allMatch(selections::contains)) {
				interactionsLeft.add(interaction);
			} else {
				interactionsRight.add(interaction);
			}
		});
	}
	

	// test to only create one configuration containing about half of the possible
	// interactions
	private List<LiteralList> binarySearch(List<LiteralList> possibleInteractions, int numberOfFeatures) {

		List<LiteralList> interactionsAll = possibleInteractions;
		int configCount = 0;
		int maxConfig = (int) (2* Math.ceil(2*Math.log(numberOfFeatures)) + numberOfFeatures);
		System.out.println("---------- " + maxConfig);
		while (interactionsAll.size() > 1 && configCount < maxConfig) {
			addInteractionCount(interactionsAll.size());
			final List<LiteralList> interactionsLeft = new ArrayList<>(interactionsAll.size());
			final List<LiteralList> interactionsRight = new ArrayList<>(interactionsAll.size());

			final LiteralList configuration = findConfig(interactionsAll, interactionsLeft, interactionsRight);

//			LiteralList configuration_ = null;
//			do {
//				configuration_ = getConfiguration(interactionsLeft);
//			} while (!configuration_.containsAll(merge));
//			final LiteralList configuration = configuration_;

			if (configuration == null) {
				return interactionsAll;
			}

			if (verifier.test(configuration)) {
				validConfs.add(configuration);
				// HERE update interactionsAll dass FIs aus validConfs nicht mehr möglich ist
				interactionsAll = interactionsAll.stream() //
						.filter(combo -> !configuration.containsAll(combo)) //
						.collect(Collectors.toList());
			} else {
				failingConfs.add(configuration);
				// HERE update interactionsAll dass nur FIs aus allen bisherigen failingConfs
				// möglich sind
				interactionsAll = interactionsAll.stream() //
						.filter(combo -> configuration.containsAll(combo)) //
						.collect(Collectors.toList());
			}
			configCount++;
		}
		return !interactionsAll.isEmpty() //
				? interactionsAll //
				: new ArrayList<>();

	}

	private LiteralList findConfig(List<LiteralList> interactionsAll, final List<LiteralList> interactionsLeft,
			final List<LiteralList> interactionsRight) {
		LiteralList configuration = null;
		for (int i = -1; i < interactionsAll.size(); i++) {
			if (i == -1) {
				split(interactionsAll, interactionsLeft, interactionsRight);
			} else {
				split(interactionsAll, interactionsLeft, interactionsRight, i);
			}
			configuration = updater
					.complete(LiteralList.merge(interactionsLeft), Arrays.asList(LiteralList.merge(interactionsRight)))
					.orElse(null);
			if (configuration != null) {
				break;
			}
		}
		return configuration;
	}

}
