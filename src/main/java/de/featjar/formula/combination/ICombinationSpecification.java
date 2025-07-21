/*
 * Copyright (C) 2025 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
package de.featjar.formula.combination;

import de.featjar.formula.VariableMap;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ICombinationSpecification {

    long loopCount();

    void shuffleElements(Random random);

    void adapt(VariableMap variableMap);

    void forEach(Consumer<int[]> consumer);

    <V> void forEach(BiConsumer<V, int[]> consumer, Supplier<V> environmentCreator);

    <V> void forEachParallel(BiConsumer<V, int[]> consumer, Supplier<V> environmentCreator);

    VariableMap variableMap();

    int maxT();

    ICombinationSpecification reduceTTo(int newT);
}
