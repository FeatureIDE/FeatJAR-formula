package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.value.ValueClause;

public class ComputeBooleanRepresentationOfClause extends AComputeBooleanRepresentation<ValueClause, BooleanClause> {
    public ComputeBooleanRepresentationOfClause(IComputation<ValueClause> valueRepresentation) {
        super(valueRepresentation);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeBooleanRepresentationOfClause(getInput());
    }
}
