package webservices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UniversalResourceIdentifer {

	// from: https://stackoverflow.com/questions/14321873/java-url-encoding-urlencoder-vs-uri
	public static String encodeURIComponent(String s) {
	    String result;

	    try {
	        result = URLEncoder.encode(s, "UTF-8")
	                .replaceAll("\\+", "%20")
	                .replaceAll("\\%21", "!")
	                .replaceAll("\\%27", "'")
	                .replaceAll("\\%28", "(")
	                .replaceAll("\\%29", ")")
	                .replaceAll("\\%7E", "~");
	    } catch (UnsupportedEncodingException e) {
	        result = s;
	    }

	    return result;
	}
	
	
}
