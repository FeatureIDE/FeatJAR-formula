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

import de.featjar.base.data.Result;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.IConnective;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.IPredicate;

import java.util.List;

/**
 * Tests whether a formula is in (clausal) normal form.
 * Clausal normal form is a special case of each normal form and usually easier to process in an automated fashion.
 * Thus, we usually allow normal forms as input and use clausal normal form as output.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class ANormalFormTester implements ITreeVisitor<IFormula, Boolean> {

    protected boolean isNormalForm = true;
    protected boolean isClausalNormalForm = true;

    @Override
    public void reset() {
        isNormalForm = true;
        isClausalNormalForm = true;
    }

    @Override
    public Result<Boolean> getResult() {
        return Result.of(isNormalForm);
    }

    public boolean isNormalForm() {
        return isNormalForm;
    }

    public boolean isClausalNormalForm() {
        return isClausalNormalForm;
    }

    protected TraversalAction processLevelOne(List<IFormula> path, IFormula formula, Class<? extends IConnective> connectiveClass) {
        if (path.size() > 1) {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
        for (final IExpression child : formula.getChildren()) {
            if (!connectiveClass.isInstance(child)) {
                if (!(child instanceof IPredicate)) {
                    isNormalForm = false;
                    isClausalNormalForm = false;
                    return TraversalAction.SKIP_ALL;
                }
                isClausalNormalForm = false;
            }
        }
        return TraversalAction.CONTINUE;
    }

    protected TraversalAction processLevelTwo(List<IFormula> path, IFormula formula) {
        if (path.size() > 2) {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
        if (path.size() < 2) {
            isClausalNormalForm = false;
        }
        for (final IExpression child : formula.getChildren()) {
            if (!(child instanceof IPredicate)) {
                isNormalForm = false;
                isClausalNormalForm = false;
                return TraversalAction.SKIP_ALL;
            }
        }
        return TraversalAction.CONTINUE;
    }

    protected TraversalAction processLevelThree(List<IFormula> path) {
        if (path.size() > 3) {
            isNormalForm = false;
            isClausalNormalForm = false;
            return TraversalAction.SKIP_ALL;
        }
        if (path.size() < 3) {
            isClausalNormalForm = false;
        }
        return TraversalAction.SKIP_CHILDREN;
    }

}
