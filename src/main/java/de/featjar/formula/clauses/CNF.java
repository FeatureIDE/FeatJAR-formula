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
package de.featjar.formula.clauses;

import de.featjar.base.data.Result;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// TODO rename, as it can represent both CNF and DNF
/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public class CNF {

    protected ClauseList clauses;
    protected VariableMap variables;

    public CNF(VariableMap mapping, ClauseList clauses) {
        variables = mapping;
        this.clauses = clauses;
    }

    public CNF(VariableMap mapping, List<LiteralList> clauses) {
        variables = mapping;
        this.clauses = new ClauseList(clauses);
    }

    public CNF(VariableMap mapping) {
        variables = mapping;
        clauses = new ClauseList();
    }

    public void setClauses(ClauseList clauses) {
        this.clauses = clauses;
    }

    public void addClause(LiteralList clause) {
        clauses.add(clause);
    }

    public void addClauses(Collection<LiteralList> clauses) {
        this.clauses.addAll(clauses);
    }

    public void setVariableMap(VariableMap variables) {
        this.variables = variables;
    }

    public VariableMap getVariableMap() {
        return variables;
    }

    public ClauseList getClauses() {
        return clauses;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, clauses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final CNF other = (CNF) obj;
        return Objects.equals(variables, other.variables) && Objects.equals(clauses, other.clauses);
    }

    @Override
    public String toString() {
        return "CNF\n\tvariables=" + variables + "\n\tclauses=" + clauses;
    }

    /**
     * Creates a new clause list from this CNF with all clauses adapted to a new
     * variable mapping.
     *
     * @param newTermMap the new variables
     * @return an adapted cnf, {@code null} if there are old variables names the are
     *         not contained in the new variables.
     */
    public Result<CNF> adapt(VariableMap newTermMap) {
        return clauses.adapt(variables, newTermMap).map(c -> new CNF(newTermMap, c));
    }

    public CNF randomize(Random random) {
        final VariableMap newTermMap = (VariableMap) variables.clone();
        newTermMap.randomize(random);

        final ClauseList adaptedClauseList =
                clauses.adapt(variables, newTermMap).get();
        Collections.shuffle(adaptedClauseList, random);

        return new CNF(newTermMap, adaptedClauseList);
    }
}
