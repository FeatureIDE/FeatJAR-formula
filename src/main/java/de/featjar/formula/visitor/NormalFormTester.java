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
package de.featjar.formula.visitor;

import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.formula.Formula;

import java.util.Optional;

/**
 * Tests whether a formula is in a normal form.
 *
 * @author Sebastian Krieter
 */
public abstract class NormalFormTester implements TreeVisitor<Formula, Boolean> {

    protected boolean isNormalForm = true;
    protected boolean isClausalNormalForm = true;

    @Override
    public void reset() {
        isNormalForm = true;
        isClausalNormalForm = true;
    }

    @Override
    public Optional<Boolean> getResult() {
        return Optional.of(isNormalForm);
    }

    public boolean isNormalForm() {
        return isNormalForm;
    }

    public boolean isClausalNormalForm() {
        return isClausalNormalForm;
    }
}
