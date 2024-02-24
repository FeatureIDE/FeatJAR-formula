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
package de.featjar.formula.io.dimacs;

/**
 * Constants for the DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class DimacsConstants {
    /** Token leading a (single-line) comment. */
    public static final String COMMENT = "c";

    public static final String COMMENT_START = COMMENT + " ";
    /** Token leading the problem definition. */
    public static final String PROBLEM = "p";
    /** Token identifying the problem type as CNF. */
    public static final String CNF = "cnf";
    /** Token denoting the end of a clause. */
    public static final String CLAUSE_END = "0";
}
