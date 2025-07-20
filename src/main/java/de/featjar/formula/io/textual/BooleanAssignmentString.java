package de.featjar.formula.io.textual;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanAssignment;

public class BooleanAssignmentString implements IFormat<BooleanAssignment>{
	@Override
	public Result<String> serialize(BooleanAssignment ba) {		
        return Result.of(ba.toString());
    }
	
	@Override
	public BooleanAssignmentString getInstance() {
		return this;
	}

	@Override
	public String getName() {
		return "BooleanAssignment";
	}
	
    @Override
    public boolean supportsSerialize() {
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
