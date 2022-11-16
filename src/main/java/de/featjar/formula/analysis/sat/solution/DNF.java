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
package de.featjar.formula.analysis.sat.solution;

import de.featjar.base.data.Result;
import de.featjar.formula.analysis.sat.VariableMap;

import java.util.Collection;
import java.util.Objects;

/**
 * A list of solutions to a satisfiability problem stored in indexed disjunctive normal form.
 * Compared to a {@link de.featjar.formula.structure.formula.Formula} in DNF (e.g., computed with
 * {@link de.featjar.formula.transformer.ToDNFFormula}), a {@link DNF} is a more low-level
 * representation of the same formula as a list of solutions (i.e., lists of literal indices).
 * That is, the clause list only contains indices into a {@link VariableMap}, which links
 * a {@link DNF} to the {@link de.featjar.formula.structure.term.value.Variable variables}
 * in the original {@link de.featjar.formula.structure.formula.Formula}.
 * TODO: more error checking for consistency of clauses with variables
 * TODO: maybe rename to IndexedCNF?
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class DNF {
    protected SolutionList solutionList;
    protected VariableMap variableMap;

    public DNF() {
        this(new SolutionList(), VariableMap.empty());
    }

    public DNF(SolutionList solutionList) {
        this(solutionList, VariableMap.empty());
    }

    public DNF(VariableMap variableMap) {
        this(new SolutionList(), variableMap);
    }

    public DNF(Collection<SATSolution> SATSolutions, VariableMap variableMap) {
        this(new SolutionList(SATSolutions), variableMap);
    }

    public DNF(SolutionList solutionList, VariableMap variableMap) {
        this.solutionList = solutionList;
        this.variableMap = variableMap;
    }

    public SolutionList getSolutionList() {
        return solutionList;
    }

    public void setSolutionList(SolutionList solutionList) {
        this.solutionList = solutionList;
    }

    public void addSolution(SATSolution SATSolution) {
        solutionList.add(SATSolution);
    }

    public void addSolutions(Collection<SATSolution> SATSolutions) {
        solutionList.addAll(SATSolutions);
    }

    public VariableMap getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(VariableMap variableMap) {
        Result<SolutionList> newClauseList = solutionList.adapt(this.variableMap, variableMap);
        if (newClauseList.isPresent()) {
            this.solutionList = newClauseList.get();
            this.variableMap = variableMap;
        } else
            throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNF cnf = (DNF) o;
        return Objects.equals(solutionList, cnf.solutionList) && Objects.equals(variableMap, cnf.variableMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solutionList, variableMap);
    }

//    public CNF randomize(Random random) {
//        final VariableMap newTermMap = (VariableMap) variableMap.clone();
//        newTermMap.randomize(random);
//
//        final LiteralMatrix adaptedLiteralMatrix =
//                clauseList.adapt(variableMap, newTermMap).get();
//        Collections.shuffle(adaptedLiteralMatrix, random);
//
//        return new CNF(newTermMap, adaptedLiteralMatrix);
//    }
}
