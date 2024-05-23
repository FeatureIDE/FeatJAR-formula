/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.Computations;
import de.featjar.base.computation.IComputation;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.IValueRepresentation;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.transform.ComputeCNFFormula;
import de.featjar.formula.transform.ComputeDNFFormula;
import de.featjar.formula.transform.ComputeNNFFormula;

public interface IBooleanRepresentation {

    public static IComputation<BooleanClauseList> toBooleanClauseList(IFormula model) {
        return toBooleanCNFRepresentation(model).map(Computations::getKey).cast(BooleanClauseList.class);
    }

    public static IComputation<VariableMap> toVariableMap(IFormula model) {
        return toBooleanCNFRepresentation(model).map(Computations::getValue).cast(VariableMap.class);
    }

    public static ComputeBooleanRepresentation<IFormula> toBooleanCNFRepresentation(IFormula model) {
        return Computations.of(model)
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(ComputeBooleanRepresentation::new);
    }

    public static ComputeBooleanRepresentation<IFormula> toBooleanDNFRepresentation(IFormula model) {
        return Computations.of(model)
                .map(ComputeNNFFormula::new)
                .map(ComputeDNFFormula::new)
                .map(ComputeBooleanRepresentation::new);
    }

    /**
     * {@return a value object with the same contents as this object}
     */
    IValueRepresentation toValue();
}
