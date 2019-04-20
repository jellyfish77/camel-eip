package movies;

public class EscapeSpecialCharsBean {

	public static String escapeSpecialChars(String body) {
		
		String noNbsp = body.replace("\u00A0", "");
		//String escaped =  StringEscapeUtils.escapeXml(noNbsp);
		//String escaped =  body.replace("&", "&amp;");

		return noNbsp;
		//return escaped;
		
	}
}
