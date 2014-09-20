package org.samsha.iec.util;

public class OutputResult {
	protected String shortErrorMessage;
	protected String longErrorMessage;
	protected boolean success;
	
	public OutputResult() {
		shortErrorMessage = "Success";
		longErrorMessage = "Success";
		success = true;
	}
	
	public boolean isSuccessful() {
		return success;
	}
	
	public boolean hasFailed() {
		return !success;
	}
	
	public void markAsSuccessful() {
		success = true;
	}
	
	public void markSuccessful(String shortErrorMessage, String longErrorMessage) {
		markAsSuccessful();
		this.shortErrorMessage = shortErrorMessage;
		this.longErrorMessage = longErrorMessage;
	}
	
	public void markAsFailed(String shortErrorMessage, String longErrorMessage) {
		this.shortErrorMessage = shortErrorMessage;
		this.longErrorMessage = longErrorMessage;
		this.success = false;
	}
	
	public void markAsFailed(String errorMessage) {
		this.shortErrorMessage = errorMessage;
		this.longErrorMessage = errorMessage;
		this.success = false;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean newLine) {
		String newLineString = " ";
		if (newLine) {
			newLineString = "\n";
		}
		String value = String.format("ErrorCode: %s%sShort Description: %s%sLong Description: %s",
				new Object[] {success ? "Success":"Failed", newLineString, shortErrorMessage,
						newLineString, longErrorMessage});
		return value;
	}
	
	public String getShortErrorMessage() {
		return this.shortErrorMessage;
	}
	
	public String longErrorMessage() {
		return this.longErrorMessage;
	}
}
