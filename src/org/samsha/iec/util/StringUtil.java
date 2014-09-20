package org.samsha.iec.util;

public class StringUtil {
	public static boolean emptyString(String testString) {
		if (testString == null || testString.equals("")) {
			return true;
		}
		return false;
	}
}
