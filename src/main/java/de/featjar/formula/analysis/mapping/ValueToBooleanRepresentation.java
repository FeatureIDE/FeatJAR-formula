package de.featjar.formula.analysis.mapping;

import de.featjar.base.data.Computation;
import de.featjar.base.data.FutureResult;
import de.featjar.base.data.Pair;
import de.featjar.formula.analysis.Analysis;
import de.featjar.formula.analysis.bool.BooleanRepresentation;
import de.featjar.formula.analysis.value.ValueRepresentation;

public abstract class ValueToBooleanRepresentation implements Analysis.Unfold<ValueRepresentation, VariableMap, BooleanRepresentation> {
    protected Computation<ValueRepresentation> valueRepresentationComputation;

    public ValueToBooleanRepresentation(Computation<ValueRepresentation> valueRepresentationComputation) {
        this.valueRepresentationComputation = valueRepresentationComputation;
    }

    @Override
    public Computation<ValueRepresentation> getInput() {
        return valueRepresentationComputation;
    }

    @Override
    public ValueToBooleanRepresentation setInput(Computation<ValueRepresentation> inputComputation) {
        this.valueRepresentationComputation = inputComputation;
        return this;
    }

    @Override
    public FutureResult<Pair<VariableMap, BooleanRepresentation>> compute() {
        return valueRepresentationComputation.get().thenCompute(((valueRepresentation, monitor) -> {
            VariableMap variableMap = VariableMap.of(valueRepresentation);
            return new Pair<>(variableMap, valueRepresentation.toBoolean(variableMap).get());
        }));
    }
}
