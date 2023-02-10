/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
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
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class InteractionFinderAtLeastOne extends AbstractInteractionFinder {

    public InteractionFinderAtLeastOne(
            Collection<LiteralList> sample,
            SolutionUpdater configurationGenerator,
            Predicate<LiteralList> configurationChecker) {
        super(sample, configurationGenerator, configurationChecker);
    }

    public List<LiteralList> find(int t, int numberOfFeatures) {
        List<LiteralList> interactionsAll = computePotentialInteractions(t);
        while (interactionsAll.size() > 1) {
            addInteractionCount(interactionsAll.size());
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
        return interactionsAll;
    }

    private LiteralList findConfig(List<LiteralList> interactionsAll) {
        if (interactionsAll.size() < 5000) {
            List<LiteralList> configs = new ArrayList<>(interactionsAll.size());
            configs.addAll(getAtLeastOneConfigs(interactionsAll));
            configs.addAll(getRandomConfigs((int) Math.ceil(Math.log(interactionsAll.size()))));
            return findBestConfig(interactionsAll, configs);
        } else {
            LiteralList bestConfig = findBestConfig(
                    interactionsAll, getRandomConfigs((int) Math.ceil(2 * Math.log(interactionsAll.size()))));

            return bestConfig != null
                    ? bestConfig
                    : findBestConfig(interactionsAll, getAtLeastOneConfigs(interactionsAll));
        }
    }

    private List<LiteralList> getRandomConfigs(int numberOfConfigurations) {
        List<LiteralList> potentialConfs = new ArrayList<>();
        for (int i = 0; i < numberOfConfigurations; i++) {
            LiteralList config = complete(null);
            if (config == null) {
                break;
            }
            potentialConfs.add(config);
        }
        return potentialConfs;
    }

    private List<LiteralList> getAtLeastOneConfigs(List<LiteralList> interactionsAll) {
        List<LiteralList> potentialConfs = new ArrayList<>();
        interationLoop:
        for (int i = 0; i < interactionsAll.size(); i++) {
            LiteralList interaction = interactionsAll.get(i);
            if (potentialConfs.parallelStream()
                    .filter(c -> c.containsAll(interaction))
                    .findAny()
                    .isPresent()) {
                continue interationLoop;
            }
            List<LiteralList> interactionsRight = new ArrayList<>(interactionsAll.size() - 1);
            interactionsRight.addAll(interactionsAll.subList(0, i));
            interactionsRight.addAll(interactionsAll.subList(i + 1, interactionsAll.size()));
            LiteralList config = complete(interaction, LiteralList.merge(interactionsRight));
            if (config != null) {
                potentialConfs.add(config);
            } else {
                // TODO remove interaction from list
            }
        }
        return potentialConfs;
    }

    private LiteralList findBestConfig(List<LiteralList> interactionsAll, List<LiteralList> potentialConfs) {
        LiteralList bestConfig = null;
        double bestRatio = 0.5;
        for (LiteralList config : potentialConfs) {
            final double ratio = Math.abs(0.5
                    - (((double) interactionsAll.parallelStream()
                                    .filter(config::containsAll)
                                    .count())
                            / interactionsAll.size()));
            if (ratio < bestRatio) {
                bestRatio = ratio;
                bestConfig = config;
            }
        }
        return bestConfig;
    }
}
