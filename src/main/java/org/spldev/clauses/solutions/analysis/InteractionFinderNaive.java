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

public class InteractionFinderNaive extends AbstractInteractionFinder {

	public InteractionFinderNaive(Collection<LiteralList> sample,
		SolutionUpdater configurationGenerator,
		Predicate<LiteralList> configurationChecker) {
		super(sample, configurationGenerator, configurationChecker);
	}

	public LiteralList find(int t) {
		List<LiteralList> interactionsAll = computePotentialInterations(t);

		while (interactionsAll.size() > 1) {
			LiteralList configuration = updater.complete(null).orElse(null);
			if (configuration == null) {
				return interactionsAll.get(0);
			}
			if (verifyer.test(configuration)) {
				interactionsAll = interactionsAll.stream() //
					.filter(combo -> !configuration.containsAll(combo)) //
					.collect(Collectors.toList());
			} else {
				interactionsAll = interactionsAll.stream() //
					.filter(combo -> configuration.containsAll(combo)) //
					.collect(Collectors.toList());
			}
		}
		return interactionsAll.isEmpty() ? new LiteralList() : interactionsAll.get(0);
	}

}
