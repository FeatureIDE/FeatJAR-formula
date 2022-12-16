package de.featjar.formula.analysis.value;

import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.analysis.mapping.VariableMap;
import de.featjar.formula.transformer.ToCNF;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public ValueClauseList(ValueClauseList other) {
        super(other);
    }

    public ValueClauseList(Collection<? extends ValueClause> clauses) {
        super(clauses);
    }

    @Override
    protected ValueClauseList newAssignmentList(List<ValueClause> clauses) {
        return new ValueClauseList(clauses);
    }

    @Override
    public Result<BooleanClauseList> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @Override
    public Computation<BooleanClauseList> toBoolean(Computation<VariableMap> variableMapComputation) {
        return variableMapComputation.mapResult(variableMap -> toBoolean(variableMap).get());
    }

    @Override
    public String toString() {
        return String.format("ValueClauseList[%s]", print());
    }

    @Override
    public ValueClauseList toClauseList() {
        return new ValueClauseList(literalLists);
    }

    @Override
    public ValueSolutionList toSolutionList() {
        return new ValueSolutionList(literalLists.stream().map(ValueClause::toSolution).collect(Collectors.toList()));
    }
}
