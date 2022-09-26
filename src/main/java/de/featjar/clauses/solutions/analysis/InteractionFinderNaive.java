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
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.featjar.clauses.LiteralList;

public class InteractionFinderNaive extends AbstractInteractionFinder {

	public InteractionFinderNaive(Collection<LiteralList> sample,
									SolutionUpdater configurationGenerator,
									Predicate<LiteralList> configurationChecker) {
		super(sample, configurationGenerator, configurationChecker);
	}

	public List<LiteralList> find(int t, int numberOfFeatures) {
//		System.out.println("---------- ");
		List<LiteralList> interactionsAll = computePotentialInteractions(t);
//		System.out.println("interactionsAllSize: " + interactionsAll.size());
//		System.out.println("---------- ");

		int configCount = 0;
		int maxConfig = (int) (2* Math.round(3*(Math.log(numberOfFeatures)/Math.log(2)))) +100;
//		System.out.println("---------- " + maxConfig);
		
		while (interactionsAll.size() > 1 && configCount < maxConfig) {
			addInteractionCount(interactionsAll.size());
			//HERE create new configuration
			LiteralList configuration = updater.complete(null).orElse(null);
//			System.out.println("configuration " + configuration);
			if (configuration == null) {
				return interactionsAll;
			}
			if (verifier.test(configuration)) {
				validConfs.add(configuration);
				// HERE update interactionsAll to exlcude all fis from valid configs to be the faulty one
				interactionsAll = interactionsAll.stream() //
					.filter(combo -> !configuration.containsAll(combo)) //
					.collect(Collectors.toList());
			} else {
				failingConfs.add(configuration);
				// HERE update interactionsAll to intersect all failing fis
				interactionsAll = interactionsAll.stream() //
					.filter(combo -> configuration.containsAll(combo)) //
					.collect(Collectors.toList());
			}
			configCount++;
			
//			System.out.println(configuration);
//			System.out.println("interactionsAllSize: " +interactionsAll.size());
		}
		return interactionsAll.isEmpty() ? new ArrayList<LiteralList>() : interactionsAll;
	}

}
