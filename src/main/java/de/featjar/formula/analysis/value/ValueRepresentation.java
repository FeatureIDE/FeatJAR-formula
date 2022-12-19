package de.featjar.formula.analysis.value;

import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanRepresentation;

import java.util.Set;

public interface ValueRepresentation {
    /**
     * {@return a Boolean object with the same contents as this object}
     * If needed, translates variable indices using the given variable map.
     * The returned result may contain warnings, as this can be a lossy conversion.
     *
     * @param variableMap the variable map
     */
    Result<? extends BooleanRepresentation> toBoolean(VariableMap variableMap);

    Computation<? extends BooleanRepresentation> toBoolean(Computation<VariableMap> variableMap); // todo: lift instead using Computations.async?

    Set<String> getVariableNames(); // todo: preserve order?
}
