package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Pair;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanSolutionList;

public class ComputeValueRepresentationOfSolutionList extends AComputeValueRepresentation<BooleanSolutionList, ValueSolutionList> {
    public ComputeValueRepresentationOfSolutionList(IComputation<BooleanSolutionList> booleanRepresentation, IComputation<VariableMap> variableMap) {
        super(booleanRepresentation, variableMap);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeValueRepresentationOfSolutionList(getInput(), getVariableMap());
    }
}
