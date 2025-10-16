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
package de.featjar.formula.structure.term.aggregate;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.term.ITerm;

import java.util.List;

/**
 * Interface for modelling attribute aggregate functionality. Attribute aggregates are placeholders which
 * will be translated into the actual formula.
 *
 * @author Lara Merza
 * @author Felix Behme
 * @author Jonas Hanke
 */
public interface IAttributeAggregate extends ITerm {

    String getAttributeFilter();

    Result<IExpression> translate(List<IFormula> formulas, List<?> values);
}