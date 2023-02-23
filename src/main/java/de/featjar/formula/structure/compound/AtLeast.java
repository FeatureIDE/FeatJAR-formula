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
package de.featjar.formula.structure.compound;

import de.featjar.formula.structure.Formula;
import java.util.List;

/**
 * A logical connector that is {@code true} iff at least a given number of its
 * children are {@code true}.
 *
 * @author Sebastian Krieter
 */
public class AtLeast extends Cardinal {

    public AtLeast(List<Formula> nodes, int min) {
        super(nodes, min, Integer.MAX_VALUE);
    }

    private AtLeast(AtLeast oldNode) {
        super(oldNode);
    }

    @Override
    public AtLeast cloneNode() {
        return new AtLeast(this);
    }

    @Override
    public String getName() {
        return "atLeast-" + min;
    }

    @Override
    public int getMin() {
        return super.getMin();
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
    }
}
