package de.featjar.formula.analysis.bool;

import de.featjar.formula.analysis.value.ValueClauseList;
import de.featjar.formula.transformer.ToCNF;

import java.util.Collection;
import java.util.List;

/**
 * A list of Boolean clauses.
 * Typically used to express a conjunctive normal form.
 * Compared to a {@link de.featjar.formula.structure.formula.Formula} in CNF (e.g., computed with
 * {@link ToCNF}), a {@link ValueClauseList} is a more low-level representation.
 * A Boolean clause list only contains indices into a {@link VariableMap}, which links
 * a {@link BooleanClauseList} to the {@link de.featjar.formula.structure.term.value.Variable variables}
 * in the original {@link de.featjar.formula.structure.formula.Formula}.
 * TODO: more error checking for consistency of clauses with variables
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanClauseList extends BooleanAssignmentList<BooleanClauseList, BooleanClause> {
    public BooleanClauseList() {
    }

    public BooleanClauseList(int size) {
        super(size);
    }

    public BooleanClauseList(Collection<? extends BooleanClause> clauses) {
        super(clauses);
    }

    public BooleanClauseList(BooleanClauseList other) {
        super(other);
    }

    @Override
    protected BooleanClauseList newLiteralMatrix(List<BooleanClause> clauses) {
        return new BooleanClauseList(clauses);
    }

}
