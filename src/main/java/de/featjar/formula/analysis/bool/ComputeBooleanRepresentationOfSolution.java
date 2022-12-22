package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.value.ValueSolution;

public class ComputeBooleanRepresentationOfSolution extends AComputeBooleanRepresentation<ValueSolution, BooleanSolution> {
    public ComputeBooleanRepresentationOfSolution(IComputation<ValueSolution> valueRepresentation) {
        super(valueRepresentation);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeBooleanRepresentationOfSolution(getInput());
    }
}
