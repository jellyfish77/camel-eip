package utils;

public class Encoder {

	public static String escapeXmlChars(String str) {
		str = str.trim();
		str = str.replaceAll("\"", "&quot;");
		str = str.replaceAll("'", "&apos;");
		return str;
	}

	

}
