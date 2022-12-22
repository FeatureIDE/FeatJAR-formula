package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.transformer.TransformCNFFormula;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of value clauses.
 * Typically used to express a conjunctive normal form.
 * Compared to a {@link IFormula} in CNF (e.g., computed with
 * {@link TransformCNFFormula}), a {@link ValueClauseList} is a more low-level representation.
 *
 * @author Elias Kuiter
 */
public class ValueClauseList extends AValueAssignmentList<ValueClauseList, ValueClause> {
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

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<BooleanClauseList> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanClauseList>) super.toBoolean(variableMap);
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
