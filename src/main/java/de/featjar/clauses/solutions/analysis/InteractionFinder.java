/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.clauses.solutions.analysis;

import java.util.List;

import de.featjar.clauses.LiteralList;

/**
 * Detect interactions from given set of configurations.
 * 
 * @author Sebastian Krieter
 *
 */
public interface InteractionFinder {

	List<LiteralList> find(int t, int x);

	void setCore(LiteralList coreDead);

	int getConfigurationCount();

	List<?> getInteractionCounter();

	LiteralList getCore();

	LiteralList merge(List<LiteralList> result);

	boolean verify(LiteralList solution);

	LiteralList complete(LiteralList include, LiteralList... exclude);

	LiteralList update(LiteralList result);

	int getConfigCreationCount();

	int getVerifyCount();

}
