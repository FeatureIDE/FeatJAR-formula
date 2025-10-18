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
package de.featjar.formula.structure.predicate;

import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.term.ITerm;
import java.util.List;

/**
 * Expresses "A &lt;= B" constraints.
 * Evaluates to {@code true} iff the left child evaluates to a smaller or the same value as the right child.
 *
 * @author Sebastian Krieter
 */
public class LessEqual extends ANonTerminalExpression implements IBinaryPredicate, IInvertiblePredicate {
    protected LessEqual() {}

    public LessEqual(ITerm leftTerm, ITerm rightTerm) {
        super(leftTerm, rightTerm);
    }

    public LessEqual(List<? extends ITerm> terms) {
        super(terms);
    }

    @Override
    public String getName() {
        return "<=";
    }

    @Override
    public LessEqual cloneNode() {
        return new LessEqual();
    }

    @Override
    public GreaterThan invert() {
        return new GreaterThan((ITerm) getLeftExpression(), (ITerm) getRightExpression());
    }

    @Override
    public boolean compareDifference(int difference) {
        return difference <= 0;
    }
}
