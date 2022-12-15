package de.featjar.formula.analysis.value;

import de.featjar.base.data.Result;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.analysis.bool.VariableMap;
import de.featjar.formula.analysis.io.ValueSolutionListFormat;
import de.featjar.formula.transformer.ToCNF;

import java.util.Collection;
import java.util.List;

/**
 * A list of value clauses.
 * Typically used to express a conjunctive normal form.
 * Compared to a {@link de.featjar.formula.structure.formula.Formula} in CNF (e.g., computed with
 * {@link ToCNF}), a {@link ValueClauseList} is a more low-level representation.
 *
 * @author Elias Kuiter
 */
public class ValueClauseList extends ValueAssignmentList<ValueClauseList, ValueClause> {
    public ValueClauseList() {
    }

    public ValueClauseList(int size) {
        super(size);
    }

    public ValueClauseList(Collection<? extends ValueClause> clauses) {
        super(clauses);
    }

    public ValueClauseList(ValueClauseList other) {
        super(other);
    }

    @Override
    protected ValueClauseList newAssignmentList(List<ValueClause> clauses) {
        return new ValueClauseList(clauses);
    }

    public Result<BooleanClauseList> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @Override
    public String toString() {
        return String.format("ValueAssignment[%s]", print());
    }
}
