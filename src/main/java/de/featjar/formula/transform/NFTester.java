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
package de.featjar.formula.transform;

import de.featjar.formula.structure.Expression;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.Optional;

public class NFTester implements TreeVisitor<Expression, Boolean> {

    protected boolean isNf;
    protected boolean isClausalNf;

    @Override
    public void reset() {
        isNf = true;
        isClausalNf = true;
    }

    @Override
    public Optional<Boolean> getResult() {
        return Optional.of(isNf);
    }

    public boolean isNf() {
        return isNf;
    }

    public boolean isClausalNf() {
        return isClausalNf;
    }
}
