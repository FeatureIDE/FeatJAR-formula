package de.featjar.formula.io.textual;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanAssignmentList;

public class BooleanAssignmentListString implements IFormat<BooleanAssignmentList>{
	@Override
	public Result<String> serialize(BooleanAssignmentList bl) {		
        return Result.of(bl.toString());
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
