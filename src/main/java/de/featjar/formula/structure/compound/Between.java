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
public class Between extends Cardinal {

    public Between(List<Formula> nodes, int min, int max) {
        super(nodes, min, max);
    }

    private Between(Between oldNode) {
        super(oldNode);
    }

    @Override
    public Between cloneNode() {
        return new Between(this);
    }

    @Override
    public String getName() {
        return "between-" + min + "-" + max;
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
    }
}
