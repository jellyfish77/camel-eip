package utils;

public class Encoder {

	public static String escapeXmlChars(String str) {
		str = str.trim();
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("'", "&apos;");
		return str;
	}

	/*
	 * Escape all special chars for MySQL
	 */
	public static String escapeMySQLChars(String str) {
		str = str.trim();
		//str = str.replaceAll("'", "\\'");
		str = str.replaceAll("'", "\\\\'");
		str = str.replaceAll("\"", "\\\\\"");		
		
		return str; 
	}

}
