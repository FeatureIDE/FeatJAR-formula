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
import de.featjar.clauses.solutions.combinations.LexicographicIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * YASA sampling algorithm. Generates configurations for a given propositional
 * formula such that t-wise feature coverage is achieved.
 *
 * @author Sebastian Krieter
 */
public class SampleReducer {

    public static List<LiteralList> reduce(List<LiteralList> sample, int t) {
        if (sample.isEmpty()) {
            return new ArrayList<>();
        }
        sample = new ArrayList<>(sample);
        final int n = sample.get(0).size();
        t = (n < t) ? n : t;
        final int pow = (int) Math.pow(2, t);
        final int configLength = sample.size();
        final int[] literals = new int[t];
        final int[] containingConfigs = new int[configLength];
        int reducedConfigCount = 0;

        while (true) {
            final double[] scores = new double[configLength];
            final int[] uniqueInterations = new int[configLength];
            int remainingConfigCount = configLength - reducedConfigCount;
            List<LiteralList> remeiningSample = sample.subList(0, remainingConfigCount);
            List<LiteralList> reducedSample = sample.subList(remainingConfigCount, sample.size());

            LexicographicIterator.stream(t, n).forEach(combo -> {
                for (int i = 0; i < pow; i++) {
                    for (int k = 0; k < literals.length; k++) {
                        literals[k] =
                                (i >> k & 1) == 0 ? (combo.elementIndices[k] + 1) : -(combo.elementIndices[k] + 1);
                    }

                    if (reducedSample.stream().noneMatch(config -> config.containsAll(literals))) {
                        int count = 0;
                        for (int j = 0; j < remainingConfigCount; j++) {
                            if (remeiningSample.get(j).containsAll(literals)) {
                                containingConfigs[count++] = j;
                            }
                        }

                        if (count > 0) {
                            double score = 1.0 / count;
                            for (int j = 0; j < count; j++) {
                                scores[containingConfigs[j]] += score;
                            }
                            if (count == 1) {
                                for (int j = 0; j < count; j++) {
                                    uniqueInterations[containingConfigs[j]]++;
                                }
                            }
                        }
                    }
                }
            });

            int bestConfigIndex = -1;
            int maxUniqueInterations = 0;
            for (int i = 0; i < remainingConfigCount; i++) {
                if (maxUniqueInterations < uniqueInterations[i]) {
                    maxUniqueInterations = uniqueInterations[i];
                    bestConfigIndex = i;
                }
            }
            if (bestConfigIndex > -1) {
                reducedConfigCount++;
                Collections.swap(sample, bestConfigIndex, configLength - reducedConfigCount);
                continue;
            }

            double maxScore = 0;
            for (int i = 0; i < remainingConfigCount; i++) {
                if (maxScore < scores[i]) {
                    maxScore = scores[i];
                    bestConfigIndex = i;
                }
            }
            if (bestConfigIndex > -1) {
                reducedConfigCount++;
                Collections.swap(sample, bestConfigIndex, configLength - reducedConfigCount);
                continue;
            }
            break;
        }
        return new ArrayList<>(sample.subList(configLength - reducedConfigCount, sample.size()));
    }
}
