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
import org.spldev.clauses.LiteralList.*;
import org.spldev.clauses.solutions.combinations.*;

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
	protected final Predicate<LiteralList> verifyer;

	protected final List<LiteralList> validConfs = new ArrayList<>();
	protected final List<LiteralList> failingConfs = new ArrayList<>();

	private LiteralList core;

	public AbstractInteractionFinder(Collection<LiteralList> sample,
		SolutionUpdater configurationGenerator,
		Predicate<LiteralList> configurationChecker) {
		this.updater = configurationGenerator;
		this.verifyer = configurationChecker;
		for (LiteralList configuration : sample) {
			if (verifyer.test(configuration)) {
				validConfs.add(configuration);
			} else {
				failingConfs.add(configuration);
			}
		}
	}

	public void setCore(LiteralList core) {
		this.core = core;
	}

	protected List<LiteralList> computePotentialInterations(int t) {
		Iterator<LiteralList> iterator = failingConfs.iterator();
		LiteralList failingLiterals = iterator.next();
		while (iterator.hasNext()) {
			failingLiterals = failingLiterals.retainAll(iterator.next());
		}
		if (core != null) {
			failingLiterals = failingLiterals.removeAll(core);
		}
		final LiteralList commonLiterals = new LiteralList(failingLiterals, Order.NATURAL);

		if (commonLiterals.size() < t) {
			return Arrays.asList(commonLiterals);
		}

		final List<LiteralList> interactions = ParallelLexicographicIterator.stream(t, commonLiterals.size())
			.map(comboIndex -> {
				int[] literals = new int[comboIndex.length];
				for (int i = 0; i < comboIndex.length; i++) {
					literals[i] = commonLiterals.get(comboIndex[i]);
				}
				return new LiteralList(literals, Order.NATURAL, false);
			}).filter(combo -> {
				for (LiteralList configuration : validConfs) {
					if (configuration.containsAll(combo)) {
						return false;
					}
				}
				return true;
			}).collect(Collectors.toList());
		return interactions;
	}

	protected LiteralList getConfiguration(final List<LiteralList> interactions) {
		final LiteralList merge = LiteralList.merge(interactions);
		// TODO handle empty return value
		// TODO change order to potentially reduce number of newly generated
		// configurations
		return updater.complete(merge)
			.orElseGet(() -> validConfs.stream().filter(list -> list.containsAll(merge)).findAny()
				.orElseGet(() -> failingConfs.stream().filter(list -> list.containsAll(merge)).findAny().get()));

//		return configurationGenerator.apply(merge).get();
	}

	protected List<LiteralList> getConfigurations(final List<LiteralList> interactions1,
		final List<LiteralList> interactions2) {
		final LiteralList merge1 = LiteralList.merge(interactions1);
		final LiteralList merge2 = LiteralList.merge(interactions2);
		LiteralList c1, c2;

		c1 = failingConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
		c2 = validConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);

		if (c1 != null && c2 != null) {
			return Arrays.asList(c1, c2);
		}

		c1 = validConfs.stream().filter(list -> list.containsAll(merge1)).findAny().orElse(null);
		c2 = failingConfs.stream().filter(list -> list.containsAll(merge2)).findAny().orElse(null);

		if (c1 != null && c2 != null) {
			return Arrays.asList(c1, c2);
		}

		c1 = updater.complete(merge1).orElse(null);
		if (c1 != null) {
			if (!verifyer.test(c1)) {
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
			if (!verifyer.test(c2)) {
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
		return null;
	}

	protected static int maxVariable(LiteralList l) {
		if (l.size() == 1) {
			return l.get(0);
		} else {
			return Math.max(Math.abs(l.get(0)), Math.abs(l.get(l.size() - 1)));
		}
	}

}
