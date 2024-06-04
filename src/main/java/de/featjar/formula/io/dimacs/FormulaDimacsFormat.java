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

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.ParseProblem;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.VariableMap;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.Or;
import de.featjar.formula.structure.connective.Reference;
import de.featjar.formula.structure.predicate.Literal;
import java.text.ParseException;

/**
 * Reads and writes feature models in the DIMACS CNF format.
 *
 * @author Sebastian Krieter
 * @author Timo Günther
 */
public class FormulaDimacsFormat implements IFormat<IFormula> {

    @Override
    public Result<String> serialize(IFormula formula) {
        VariableMap variableMap = VariableMap.of(formula);
        return writeDIMACS(
                (formula instanceof Reference) ? ((Reference) formula).getExpression() : formula, variableMap);
    }

    private Result<String> writeDIMACS(IFormula formula, VariableMap variableMap) {
        if (!formula.isCNF()) {
            return Result.empty(new IllegalArgumentException("Formula is not in CNF"));
        }
        final StringBuilder sb = new StringBuilder();
        writeVariableDirectory(sb, variableMap);
        writeProblem(sb, formula, variableMap);
        writeClauses(sb, formula, variableMap);
        return Result.of(sb.toString());
    }

    /**
     * Writes the variable directory.
     *
     * @param sb the string builder that builds the document
     */
    private void writeVariableDirectory(StringBuilder sb, VariableMap variableMap) {
        variableMap.stream().forEach(p -> writeVariableDirectoryEntry(sb, p.getKey(), p.getValue()));
    }

    /**
     * Writes an entry of the variable directory.
     *
     * @param sb       the string builder that builds the document
     * @param index    index of the variable
     */
    private void writeVariableDirectoryEntry(StringBuilder sb, int index, String name) {
        sb.append(DimacsConstants.COMMENT_START);
        sb.append(index);
        sb.append(' ');
        sb.append(name);
        sb.append(System.lineSeparator());
    }

    /**
     * Writes the problem description.
     *
     * @param sb the string builder that builds the document
     */
    private void writeProblem(StringBuilder sb, IFormula formula, VariableMap variableMap) {
        sb.append(DimacsConstants.PROBLEM);
        sb.append(' ');
        sb.append(DimacsConstants.CNF);
        sb.append(' ');
        sb.append(variableMap.getVariableCount());
        sb.append(' ');
        sb.append(formula.getChildrenCount());
        sb.append(System.lineSeparator());
    }

    /**
     * Writes the given clause.
     *
     * @param sb     the string builder that builds the document
     * @param clause clause to transform; not null
     */
    private void writeClause(StringBuilder sb, Or clause, VariableMap variableMap) {
        for (final IExpression child : clause.getChildren()) {
            final Literal l = (Literal) child;
            final Integer index = variableMap.get(l.getExpression().getName()).orElseThrow();
            sb.append(l.isPositive() ? index : -index);
            sb.append(' ');
        }
        sb.append(DimacsConstants.CLAUSE_END);
        sb.append(System.lineSeparator());
    }

    /**
     * Writes all clauses.
     *
     * @param sb the string builder that builds the document
     */
    private void writeClauses(StringBuilder sb, IFormula formula, VariableMap variableMap) {
        for (final IExpression clause : formula.getChildren()) {
            writeClause(sb, (Or) clause, variableMap);
        }
    }

    @Override
    public Result<IFormula> parse(AInputMapper inputMapper) {
        final FormulaDimacsParser r = new FormulaDimacsParser();
        r.setReadingVariableDirectory(true);
        try {
            return Result.of(r.parse(inputMapper.get().getNonEmptyLineIterator()));
        } catch (final ParseException e) {
            return Result.empty(new ParseProblem(e, e.getErrorOffset()));
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "DIMACS";
    }

    @Override
    public String getFileExtension() {
        return "dimacs";
    }
}
