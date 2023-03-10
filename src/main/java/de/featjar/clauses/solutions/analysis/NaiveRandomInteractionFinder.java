/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.clauses.solutions.analysis;

import de.featjar.clauses.LiteralList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class NaiveRandomInteractionFinder extends AInteractionFinder {

    public List<LiteralList> find(int t) {
        if (t <= 0) {
            statistics.add(new Statistic(0, 0, 0, 0, 0));
            return Collections.emptyList();
        }
        List<LiteralList> interactionsAll = computePotentialInteractions(t);

        while (interactionsAll.size() > 1 //
                && verifyCounter < configurationVerificationLimit //
                && creationCounter < configurationCreationLimit) {
            statistics.add(
                    new Statistic(t, interactionsAll.size(), creationCounter, verifyCounter, iterationCounter++));

            final LiteralList configuration = findConfig(interactionsAll);

            if (configuration != null) {
                Stream<LiteralList> interactionStream = interactionsAll.parallelStream();
                interactionStream = verify(configuration) //
                        ? interactionStream.filter(combo -> !configuration.containsAll(combo)) //
                        : interactionStream.filter(combo -> configuration.containsAll(combo));
                interactionsAll = interactionStream.collect(Collectors.toList());
            } else {
                break;
            }
        }
        statistics.add(new Statistic(t, interactionsAll.size(), creationCounter, verifyCounter, iterationCounter));

        return interactionsAll;
    }

    protected LiteralList findConfig(List<LiteralList> interactionsAll) {
        List<LiteralList> configs = new ArrayList<>(interactionsAll.size());
        configs.addAll(getRandomConfigs(1));
        return findBestConfig(interactionsAll, configs);
    }
}
