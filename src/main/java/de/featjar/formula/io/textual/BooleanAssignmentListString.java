package de.featjar.formula.io.textual;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanAssignmentList;

public class BooleanAssignmentListString implements IFormat<BooleanAssignmentList>{
	@Override
	public Result<String> serialize(BooleanAssignmentList booleanAssignmentList) {		
        return Result.of(String.valueOf(booleanAssignmentList));
    }
	
	@Override
	public BooleanAssignmentListString getInstance() {
		return this;
	}

	@Override
	public String getName() {
		return "BooleanAssignmentList";
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
