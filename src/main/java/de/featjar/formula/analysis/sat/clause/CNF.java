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
package de.featjar.formula.analysis.sat.clause;

import de.featjar.base.data.Result;
import de.featjar.formula.analysis.sat.VariableMap;

import java.util.Collection;
import java.util.Objects;

/**
 * A satisfiability problem stored in indexed conjunctive normal form.
 * Compared to a {@link de.featjar.formula.structure.formula.Formula} in CNF (e.g., computed with
 * {@link de.featjar.formula.transformer.ToCNFFormula}), a {@link CNF} is a more low-level
 * representation of the same formula as a list of clauses (i.e., lists of literal indices).
 * That is, the clause list only contains indices into a {@link VariableMap}, which links
 * a {@link CNF} to the {@link de.featjar.formula.structure.term.value.Variable variables}
 * in the original {@link de.featjar.formula.structure.formula.Formula}.
 * TODO: more error checking for consistency of clauses with variables
 * TODO: maybe rename to IndexedCNF?
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class CNF {
    protected ClauseList clauseList;
    protected VariableMap variableMap;

    public CNF() {
        this(new ClauseList(), VariableMap.empty());
    }

    public CNF(ClauseList clauseList) {
        this(clauseList, VariableMap.empty());
    }

    public CNF(VariableMap variableMap) {
        this(new ClauseList(), variableMap);
    }

    public CNF(Collection<Clause> clauses, VariableMap variableMap) {
        this(new ClauseList(clauses), variableMap);
    }

    public CNF(ClauseList clauseList, VariableMap variableMap) {
        this.clauseList = clauseList;
        this.variableMap = variableMap;
    }

    public ClauseList getClauseList() {
        return clauseList;
    }

    public void setClauseList(ClauseList clauseList) {
        this.clauseList = clauseList;
    }

    public void addClause(Clause clause) {
        clauseList.add(clause);
    }

    public void addClauses(Collection<Clause> clauses) {
        clauseList.addAll(clauses);
    }

    public VariableMap getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(VariableMap variableMap) {
        Result<ClauseList> newClauseList = clauseList.adapt(this.variableMap, variableMap);
        if (newClauseList.isPresent()) {
            this.clauseList = newClauseList.get();
            this.variableMap = variableMap;
        } else
            throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CNF cnf = (CNF) o;
        return Objects.equals(clauseList, cnf.clauseList) && Objects.equals(variableMap, cnf.variableMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clauseList, variableMap);
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
