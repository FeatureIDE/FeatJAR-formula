package de.featjar.formula.io.textual;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanAssignmentList;

public class BooleanAssignmentGroupsString implements IFormat<BooleanAssignmentGroups>{
	@Override
	public Result<String> serialize(BooleanAssignmentGroups booleanAssignmentGroups) {		
        return Result.of(String.valueOf(booleanAssignmentGroups));
    }
	
	@Override
	public BooleanAssignmentGroupsString getInstance() {
		return this;
	}

	@Override
	public String getName() {
		return "BooleanAssignmentGroup";
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
