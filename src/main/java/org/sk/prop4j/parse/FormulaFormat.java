package org.sk.prop4j.parse;

import org.sk.prop4j.structure.*;
import org.sk.utils.io.formats.*;

public class FormulaFormat implements Format<Formula> {

	public static final String ID = FormulaFormat.class.getCanonicalName();

	@Override
	public ParseResult<Formula> parse(CharSequence source) {
		return ParseResult.of(new NodeReader().stringToNode(source.toString()));
	}

	@Override
	public String serialize(Formula object) {
		return new NodeWriter().nodeToString(object);
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getFileExtension() {
		return "formula";
	}

	@Override
	public String getName() {
		return "Formula";
	}

}
