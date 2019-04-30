package movies;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class MovieCsvToXmlConverter {

	public static String convertCsvToXml(String body) throws Exception {

		Logger LOG = LoggerFactory.getLogger(MovieCsvToXmlConverter.class);

		StringBuffer sb = new StringBuffer();
		String[] fields = body.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // handle quotes as text-delimiter
		// String numPosterFaces = ((fields[15].toString().trim() == "") ? "0" :
		// fields[15].toString().trim());
		LOG.info("CSV line: " + body);
		if (body==null || body.length() < 1) { 
			LOG.info("CSV line is empty or null, length: " + body.length() + ", fields length: " + fields.length);
			return null;
		}

		try {
			// sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<Movie>");
			LOG.info("Title before URL encode: " + fields[11].toString());
			sb.append("<Title>" + fields[11].toString().trim().replaceAll("\u00a0", "").replaceAll("^\"|\"$", "")
					+ "</Title>");
			LOG.info("Title after URL encode: " + fields[11].toString());
			sb.append("<Gross>" + fields[8].toString().trim() + "</Gross>");
			sb.append("<Genres>" + processValues(fields[9].toString().trim(), "|", "Genre") + "</Genres>");
			sb.append("<Color>" + fields[0].toString().trim() + "</Color>");
			sb.append("<UserVotes>" + fields[12].toString().trim() + "</UserVotes>");
			sb.append("<NumUsersReviewed>" + fields[18].toString().trim() + "</NumUsersReviewed>");
			sb.append("<Language>" + fields[19].toString().trim() + "</Language>");
			sb.append("<Rating>" + fields[21].toString().trim() + "</Rating>");
			sb.append("<Budget>" + ((fields[22].toString().trim() == "") ? "0" : fields[22].toString().trim())
					+ "</Budget>");
			sb.append("<Year>" + fields[23].toString().trim() + "</Year>");
			sb.append("<ImdbScore>" + fields[25].toString().trim() + "</ImdbScore>");
			sb.append("<ImdbLink>" + fields[17].toString().trim() + "</ImdbLink>");
			sb.append("<AspectRatio>" + fields[26].toString().trim() + "</AspectRatio>");
			sb.append("<Country>" + fields[20].toString().trim() + "</Country>");
			sb.append("<FacebookLikes>" + fields[27].toString().trim() + "</FacebookLikes>");
			sb.append("<NumPosterFaces>" + ((fields[15].toString().trim() == "") ? "0" : fields[15].toString().trim())
					+ "</NumPosterFaces>");
			sb.append("<PlotKeywords>" + processValues(fields[16].toString().trim(), "|", "PlotKeyword")
					+ "</PlotKeywords>");
			sb.append("<Director>");
			sb.append("<Name>" + fields[1].toString().trim() + "</Name>");
			sb.append("<FacebookLikes>" + fields[4].toString().trim() + "</FacebookLikes>");
			sb.append("</Director>");
			sb.append("<NumReviews>" + fields[2].toString().trim() + "</NumReviews>");
			sb.append("<Duration>" + fields[3].toString().trim() + "</Duration>");
			sb.append("<Cast>");
			sb.append("<Actors>");
			sb.append("<Actor>");
			sb.append("<Biling>1</Biling>");
			sb.append("<Name>" + utils.Encoder.escapeXmlChars(fields[10]) + "</Name>");
			sb.append("<FacebookLikes>" + fields[7].toString().trim() + "</FacebookLikes>");
			sb.append("</Actor>");
			sb.append("<Actor>");
			sb.append("<Biling>2</Biling>");
			sb.append("<Name>" + utils.Encoder.escapeXmlChars(fields[6]) + "</Name>");
			sb.append("<FacebookLikes>" + fields[24].toString().trim() + "</FacebookLikes>");
			sb.append("</Actor>");
			sb.append("<Actor>");
			sb.append("<Biling>3</Biling>");
			sb.append("<Name>" + utils.Encoder.escapeXmlChars(fields[14]) + "</Name>");
			sb.append("<FacebookLikes>" + fields[5].toString().trim() + "</FacebookLikes>");
			sb.append("</Actor>");
			sb.append("</Actors>");
			sb.append("</Cast>");
			sb.append("</Movie>");
		} catch (Exception e) {
			LOG.info("Caught exception converting CSV to XML");
			//throw e;
			return null;
		}

		LOG.info("Converted '" + fields[11].toString().replaceAll("\u00a0", "").replaceAll("^\"|\"$", "").trim()
				+ "' to XML");
		return sb.toString();
	}

	public static String processValues(String values, String delim, String element) {
		if (values.length() == 0) {
			return "";
		} else {
			String[] splitValues = values.split(Pattern.quote(delim));
			StringBuffer sb = new StringBuffer();
			// sb.append("<" + element + ">");
			for (String value : splitValues) {
				sb.append("<" + element + ">" + value.toString().trim() + "</" + element + ">");
			}
			// sb.append("</" + element + ">");
			return sb.toString();
		}
	}

	public String addRootNode(@Body String body) {
		return "<Movies>" + body + "</Movies>";
	}
	
}
