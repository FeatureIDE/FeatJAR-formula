package de.featjar.formula.analysis;

import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;

/**
 * An analysis that can be passed a variable map.
 * Assumes that the implementing class can be cast to {@link IComputation}.
 */
public interface IVariableMapDependency {
    Dependency<VariableMap> getVariableMapDependency();

    /**
     * {@return a computation for the variable map used by this analysis}
     */
    default IComputation<VariableMap> getVariableMap() {
        return getVariableMapDependency().get((IComputation<?>) this);
    }

    /**
     * Sets the computation for the variable map assumed by this analysis.
     *
     * @param variableMap the variable map computation
     * @return this analysis
     */
    default IVariableMapDependency setVariableMap(IComputation<VariableMap> variableMap) {
        getVariableMapDependency().set((IComputation<?>) this, variableMap);
        return this;
    }
}
