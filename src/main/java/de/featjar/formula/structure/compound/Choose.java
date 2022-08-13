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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.formula.structure.compound;

import de.featjar.formula.structure.Formula;
import java.util.List;

/**
 * A logical connector that is {@code true} iff the number of its children that
 * are {@code true} is equal to a given number.
 *
 * @author Sebastian Krieter
 */
public class Choose extends Cardinal {

    public Choose(List<Formula> nodes, int k) {
        super(nodes, k, k);
    }

    private Choose(Choose oldNode) {
        super(oldNode);
    }

    @Override
    public Choose cloneNode() {
        return new Choose(this);
    }

    @Override
    public String getName() {
        return "choose-" + min;
    }

    public void setK(int k) {
        super.setMin(k);
        super.setMax(k);
    }

    public int getK() {
        return super.getMin();
    }
}
