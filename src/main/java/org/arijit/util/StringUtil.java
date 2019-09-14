package org.arijit.util;

public class StringUtil {

	/**
	 * This method will start searching for the common part between two prefix and
	 * will return the index till the point both prefix matches
	 * 
	 * @param prefix1
	 * @param prefix2
	 * @return >-1 if matches upto certain index -1 if no match Found
	 */
	public static int findCommonPrefix(String prefix1, String prefix2) {
		int i = 0;
		int j = 0;
		while (i < prefix1.length() && j < prefix2.length()) {
			if (prefix1.charAt(i) != prefix2.charAt(j))
				break;
			i++;
			j++;
		}
		return (i - 1);
	}
}
