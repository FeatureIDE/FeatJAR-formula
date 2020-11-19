package org.spldev.formula.parse;

public class ErrorType {

	public enum ErrorEnum {
		None, InvalidFeatureName, InvalidExpressionLeft, InvalidExpressionRight, Default
	}

	private ErrorEnum error;
	private int startErrorIndex;
	private int endErrorIndex;
	private String keyword;

	public ErrorType(ErrorEnum error, int startErrorIndex, int endErrorIndex) {
		setError(error);
		setStartErrorIndex(startErrorIndex);
		setEndErrorIndex(endErrorIndex);
	}

	public ErrorType(ErrorEnum error) {
		setError(error);
	}

	public ErrorType(ErrorEnum error, String keyword) {
		setError(error);
		setKeyword(keyword);
	}

	public ErrorEnum getError() {
		return error;
	}

	public void setError(ErrorEnum error) {
		this.error = error;
	}

	public int getStartErrorIndex() {
		return startErrorIndex;
	}

	public void setStartErrorIndex(int startErrorIndex) {
		this.startErrorIndex = startErrorIndex;
	}

	public int getEndErrorIndex() {
		return endErrorIndex;
	}

	public void setEndErrorIndex(int endErrorIndex) {
		this.endErrorIndex = endErrorIndex;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
