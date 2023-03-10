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
import java.util.List;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class SingleInteractionFinder extends AInteractionFinder {

    protected LiteralList findConfig(List<LiteralList> interactionsAll) {
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

    private List<LiteralList> getAtLeastOneConfigs(List<LiteralList> interactionsAll) {
        List<LiteralList> potentialConfs = new ArrayList<>();
        interactionLoop:
        for (int i = 0; i < interactionsAll.size(); i++) {
            LiteralList interaction = interactionsAll.get(i);
            if (potentialConfs.parallelStream()
                    .filter(c -> c.containsAll(interaction))
                    .findAny()
                    .isPresent()) {
                continue interactionLoop;
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
}
