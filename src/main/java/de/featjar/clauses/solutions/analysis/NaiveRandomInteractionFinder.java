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
import java.util.List;

/**
 * Detect interactions from given set of configurations.
 *
 * @author Sebastian Krieter
 */
public class NaiveRandomInteractionFinder extends AInteractionFinder {

    @Override
    protected List<LiteralList> computePotentialInteractions(int t) {
        List<LiteralList> interactions = super.computePotentialInteractions(t);
        configurationVerificationLimit = (int) Math.ceil(10 * (Math.log(interactions.size()) / Math.log(2)) + 1);
        return interactions;
    }

    protected LiteralList findConfig(List<LiteralList> interactionsAll) {
        final List<LiteralList> randomConfigs = getRandomConfigs(1);
        return randomConfigs.isEmpty() ? null : randomConfigs.get(0);
    }
}
