package de.featjar.formula.analysis.sat.clause;

import de.featjar.formula.analysis.sat.LiteralMatrix;
import de.featjar.formula.analysis.solver.AssumptionList;

import java.util.Collection;
import java.util.List;

/**
 * A list of clauses.
 * Typically used to express a {@link CNF}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class ClauseList extends LiteralMatrix<ClauseList, SATClause> implements AssumptionList<SATClause> { // todo , ClauseList<Integer>
    public ClauseList() {
    }

    public ClauseList(int size) {
        super(size);
    }

    public ClauseList(Collection<? extends SATClause> clauses) {
        super(clauses);
    }

    public ClauseList(ClauseList other) {
        super(other);
    }

    @Override
    protected ClauseList newLiteralMatrix(List<SATClause> SATClauses) {
        return new ClauseList(SATClauses);
    }
}
