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
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.featjar.clauses.LiteralList;
import de.featjar.clauses.LiteralList.Order;
import de.featjar.clauses.solutions.combinations.LexicographicIterator;

/**
 * Detect interactions from given set of configurations.
 * 
 * @author Jens Meinicke
 * @author Sebastian Krieter
 *
 *         TODO how to detect A || B?
 */
public abstract class AbstractInteractionFinder implements InteractionFinder {

	protected final SolutionUpdater updater;
	protected final Predicate<LiteralList> verifier;

	// HERE create sets
	protected final List<LiteralList> validConfs = new ArrayList<>();
	protected final List<LiteralList> failingConfs = new ArrayList<>();
	protected final ArrayList<Long> interactionCounter = new ArrayList<>();

	private LiteralList core;

	public AbstractInteractionFinder(Collection<LiteralList> sample, SolutionUpdater configurationGenerator,
			Predicate<LiteralList> configurationChecker) {
		this.updater = configurationGenerator;
		this.verifier = configurationChecker;
		// HERE fill sets of valid and failing configs
		for (LiteralList configuration : sample) {
			if (verifier.test(configuration)) {
				validConfs.add(configuration);
				System.out.println("valide configs: " + validConfs);
			} else {
				failingConfs.add(configuration);
				System.out.println("failing configs: " + failingConfs);
			}
		}
	}

	public void setCore(LiteralList core) {
		this.core = core;
	}

	public ArrayList<Long> getInteractionCounter() {
		return interactionCounter;
	}

	protected void addInteractionCount(long count) {
		interactionCounter.add(count);
	}

	protected List<LiteralList> computePotentialInteractions(int t) {
		// HERE wollen erstmal nur für t=2
		if (t == 2) {
			Iterator<LiteralList> iterator = failingConfs.iterator();
			LiteralList failingLiterals = iterator.next();
//			System.out.println("failingLiterals: " + failingLiterals);
			while (iterator.hasNext()) {
				failingLiterals = failingLiterals.retainAll(iterator.next());
//				System.out.println("2failingLiterals: " + failingLiterals);
			}
			if (core != null) {
				failingLiterals = failingLiterals.removeAll(core);
				System.out.println("failingLiterals wihtout core: " + failingLiterals);
			}
			final LiteralList commonLiterals = new LiteralList(failingLiterals, Order.NATURAL);
//			System.out.println("1commonLiterals: " + commonLiterals);

			if (commonLiterals.size() < t) {
//				System.out.println("2commonLiterals: " + commonLiterals);
				return Arrays.asList(commonLiterals);
			}

			final List<LiteralList> interactions = LexicographicIterator.stream(t, commonLiterals.size())
//			final List<LiteralList> interactions = ParallelLexicographicIterator.stream(t, commonLiterals.size())
					.map(comboIndex -> {
//					System.out.println(Arrays.toString(comboIndex));
						int[] literals = new int[comboIndex.length];
						for (int i = 0; i < comboIndex.length; i++) {
							literals[i] = commonLiterals.get(comboIndex[i]);
//						System.out.println("int literals[" + i + "]: " + literals[i]);
						}
						return new LiteralList(literals, Order.NATURAL, false);
					}).filter(combo -> {
						for (LiteralList configuration : validConfs) {
//						System.out.println("configValids: " + configuration);
							if (configuration.containsAll(combo)) {
								return false;
							}
						}
						return true;
					}).collect(Collectors.toList());
			System.out.println("interactions: " + interactions);
			return interactions;
		} else {
			// TODO: for other values of t
			List<LiteralList> l = new ArrayList<>();
			return l;
		}
	}

	protected LiteralList getConfiguration(final List<LiteralList> interactions) {
		final LiteralList merge = LiteralList.merge(interactions);
		// TODO handle empty return value
		// TODO change order to potentially reduce number of newly generated
		// configurations
		return updater.complete(merge)
				.orElseGet(() -> validConfs.stream().filter(list -> list.containsAll(merge)).findAny().orElseGet(
						() -> failingConfs.stream().filter(list -> list.containsAll(merge)).findAny().get()));

//		return configurationGenerator.apply(merge).get();
	}

	protected LiteralList getConfiguration(List<LiteralList> include, List<LiteralList> exclude) {
		final LiteralList mergedIncludes = LiteralList.merge(include);
		// TODO handle empty return value
		// TODO change order to potentially reduce number of newly generated
		// configurations
		return updater.complete(mergedIncludes, exclude).orElseGet(
				() -> validConfs.stream().filter(list -> list.containsAll(mergedIncludes)).findAny().orElseGet(
						() -> failingConfs.stream().filter(list -> list.containsAll(mergedIncludes)).findAny().get()));

//		return configurationGenerator.apply(merge).get();
	}

	protected List<LiteralList> getConfigurations(final List<LiteralList> interactions1,
			final List<LiteralList> interactions2) {
		final LiteralList merge1 = LiteralList.merge(interactions1);
		System.out.println("merge1: " + merge1);
		final LiteralList merge2 = LiteralList.merge(interactions2);
		System.out.println("merge2: " + merge2);
		LiteralList c1, c2;

		c1 = failingConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
		c2 = validConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);

		System.out.println("1. in getConfig(leftInt,RightInt) c1: " + c1);
		System.out.println("1. in getConfig(leftInt,RightInt) c2: " + c2);

		if (c1 != null && c2 != null) {
			return Arrays.asList(c1, c2);
		}

		c1 = validConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
		c2 = failingConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);

		System.out.println("2. in getConfig(leftInt,RightInt) c1: " + c1);
		System.out.println("2. in getConfig(leftInt,RightInt) c2: " + c2);

		if (c1 != null && c2 != null) {
			return Arrays.asList(c1, c2);
		}

		c1 = updater.complete(merge1).orElse(null);
		if (c1 != null) {
			if (!verifier.test(c1)) {
				c2 = validConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);
				if (c2 != null) {
					return Arrays.asList(c1, c2);
				}
				c2 = failingConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);
				if (c2 != null) {
					return Arrays.asList(c1, c2);
				}
				c2 = updater.complete(merge2).orElse(null);
				if (c2 != null) {
					return Arrays.asList(c1, c2);
				}
			} else {
				c2 = failingConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);
				if (c2 != null) {
					return Arrays.asList(c1, c2);
				}
				c2 = updater.complete(merge2).orElse(null);
				if (c2 != null) {
					return Arrays.asList(c1, c2);
				}
			}
		}

		c2 = updater.complete(merge2).orElse(null);
		if (c2 != null) {
			if (!verifier.test(c2)) {
				c1 = validConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
				if (c1 != null) {
					return Arrays.asList(c1, c2);
				}
				c1 = failingConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
				if (c1 != null) {
					return Arrays.asList(c1, c2);
				}
			} else {
				c1 = failingConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
				if (c1 != null) {
					return Arrays.asList(c1, c2);
				}
				c1 = updater.complete(merge1).orElse(null);
				if (c1 != null) {
					return Arrays.asList(c1, c2);
				}
			}
		}
		System.out.println("end getConfigs");
		return null;
	}

	protected static int maxVariable(LiteralList l) {
		if (l.size() == 1) {
			return l.get(0);
		} else {
			return Math.max(Math.abs(l.get(0)), Math.abs(l.get(l.size() - 1)));
		}
	}

	public int getConfigurationCount() {
		return validConfs.size() + failingConfs.size();

	}

}
