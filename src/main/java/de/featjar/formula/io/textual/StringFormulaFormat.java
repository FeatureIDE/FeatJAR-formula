package de.featjar.formula.io.textual;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;

import de.featjar.formula.structure.IFormula;

public class StringFormulaFormat implements IFormat<IFormula>{

	@Override
	public Result<String> serialize(IFormula formula) {		
        return Result.of(formula.print());
    }
	
	@Override
	public StringFormulaFormat getInstance() {
		return this;
	}

	@Override
	public String getName() {
		return "featuremodel";
	}
	
    @Override
    public boolean supportsWrite() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return false;
    }
    
    @Override
    public String getFileExtension() {
        return "text";
    }
}
