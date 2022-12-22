package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.value.ValueAssignment;

public class ComputeBooleanRepresentationOfAssignment extends AComputeBooleanRepresentation<ValueAssignment, BooleanAssignment> {
    public ComputeBooleanRepresentationOfAssignment(IComputation<ValueAssignment> valueRepresentation) {
        super(valueRepresentation);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeBooleanRepresentationOfAssignment(getInput());
    }
}
