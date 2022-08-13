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
package de.featjar.formula.io.dimacs;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.compound.Or;

public class DimacsWriter {

	/**
	 * Whether the writer should write a variable directory listing the names of the
	 * variables.
	 */
	private boolean writingVariableDirectory = true;

	private final Formula formula;
	private VariableMap variables;

	/**
	 * Constructs a new instance of this class with the given CNF.
	 *
	 * @param formula the formula to transform; not null
	 * @throws IllegalArgumentException if the input is null or not in CNF
	 */
	public DimacsWriter(Formula formula) throws IllegalArgumentException {
		if ((formula == null) || !Formulas.isCNF(formula)) {
			throw new IllegalArgumentException();
		}
		this.formula = Formulas.toCNF(formula).get();
	}

	/**
	 * <p>
	 * Sets the writing variable directory flag. If true, the writer will write a
	 * variable directory at the start of the output. This is a set of comments
	 * naming the variables. This can later be used during reading so the variables
	 * are not just numbers.
	 * </p>
	 *
	 * <p>
	 * Defaults to false.
	 * </p>
	 *
	 * @param writingVariableDirectory whether to write the variable directory
	 */
	public void setWritingVariableDirectory(boolean writingVariableDirectory) {
		this.writingVariableDirectory = writingVariableDirectory;
	}

	public boolean isWritingVariableDirectory() {
		return writingVariableDirectory;
	}

	/**
	 * Writes the DIMACS CNF file format.
	 *
	 * @return the transformed CNF; not null
	 */
	public String write() {
		variables = formula.getVariableMap().orElseGet(VariableMap::new);
		final StringBuilder sb = new StringBuilder();
		if (writingVariableDirectory) {
			writeVariableDirectory(sb);
		}
		writeProblem(sb);
		writeClauses(sb);
		return sb.toString();
	}

	/**
	 * Writes the variable directory.
	 *
	 * @param sb the string builder that builds the document
	 */
	private void writeVariableDirectory(StringBuilder sb) {
		int index = 1;
		for (final String name : variables.getVariableNames()) {
			writeVariableDirectoryEntry(sb, index++, name);
		}
	}

	/**
	 * Writes an entry of the variable directory.
	 *
	 * @param sb       the string builder that builds the document
	 * @param variable variable to list in the entry
	 * @param index    index of the variable
	 */
	private void writeVariableDirectoryEntry(StringBuilder sb, int index, String name) {
		sb.append(DIMACSConstants.COMMENT_START);
		sb.append(index);
		sb.append(' ');
		sb.append(String.valueOf(name));
		sb.append(System.lineSeparator());
	}

	/**
	 * Writes the problem description.
	 *
	 * @param sb the string builder that builds the document
	 */
	private void writeProblem(StringBuilder sb) {
		sb.append(DIMACSConstants.PROBLEM);
		sb.append(' ');
		sb.append(DIMACSConstants.CNF);
		sb.append(' ');
		sb.append(variables.getVariableCount());
		sb.append(' ');
		sb.append(formula.getChildren().size());
		sb.append(System.lineSeparator());
	}

	/**
	 * Writes the given clause.
	 *
	 * @param sb     the string builder that builds the document
	 * @param clause clause to transform; not null
	 */
	private void writeClause(StringBuilder sb, Or clause) {
		for (final Formula child : clause.getChildren()) {
			final Literal l = (Literal) child;
			final Integer index = variables.getVariableIndex(l.getName()).orElseThrow(
				() -> new IllegalArgumentException(l.getName()));
			sb.append(l.isPositive() ? index : -index);
			sb.append(' ');
		}
		sb.append(DIMACSConstants.CLAUSE_END);
		sb.append(System.lineSeparator());
	}

	/**
	 * Writes all clauses.
	 *
	 * @param sb the string builder that builds the document
	 */
	private void writeClauses(StringBuilder sb) {
		for (final Formula clause : formula.getChildren()) {
			writeClause(sb, (Or) clause);
		}
	}

}
