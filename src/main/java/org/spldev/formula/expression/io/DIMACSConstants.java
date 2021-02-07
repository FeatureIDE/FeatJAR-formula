package org.spldev.formula.expression.io;

/**
 * Constants for the DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSConstants {

	/** Token leading a (single-line) comment. */
	public static final String COMMENT = "c";
	public static final String COMMENT_START = COMMENT + " ";
	/** Token leading the problem definition. */
	public static final String PROBLEM = "p";
	/** Token identifying the problem type as CNF. */
	public static final String CNF = "cnf";
	/** Token denoting the end of a clause. */
	public static final String CLAUSE_END = "0";

}
