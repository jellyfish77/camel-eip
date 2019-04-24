package movies;

import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertCsvToXml {
	
	public static String convertCsvToXml(String body) throws Exception {
		
		Logger LOG = LoggerFactory.getLogger(ConvertCsvToXml.class);				
		
		StringBuffer sb = new StringBuffer();		
		String[] fields = body.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // handle quotes as text-delimiter
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");		
		sb.append("<Movie>");
		sb.append("<Title>" + fields[11].toString().replaceAll("\u00a0", "").replaceAll("^\"|\"$", "").trim() + "</Title>");
		sb.append("<Gross>" + fields[8].toString().trim() + "</Gross>");
		sb.append("<Genres>"  + processValues(fields[9].toString().trim(), "|", "Genre") + "</Genres>");			
		sb.append("<Color>" + fields[0].toString().trim() + "</Color>");
		sb.append("<UserVotes>" + fields[12].toString().trim() + "</UserVotes>");			
		sb.append("<NumUsersReviewed>" + fields[18].toString().trim() + "</NumUsersReviewed>");			
		sb.append("<Language>" + fields[19].toString().trim() + "</Language>");
		sb.append("<Rating>" + fields[21].toString().trim() + "</Rating>");
		sb.append("<Budget>" + fields[22].toString().trim() + "</Budget>");
		sb.append("<Year>" + fields[23].toString().trim() + "</Year>");
		sb.append("<ImdbScore>" + fields[25].toString().trim() + "</ImdbScore>");
		sb.append("<ImdbLink>" + fields[17].toString().trim() + "</ImdbLink>");
		sb.append("<AspectRatio>" + fields[26].toString().trim() + "</AspectRatio>");
		sb.append("<Country>" + fields[20].toString().trim() + "</Country>");
		sb.append("<FacebookLikes>" + fields[27].toString().trim() + "</FacebookLikes>");
		sb.append("<NumPosterFaces>" + fields[15].toString().trim() + "</NumPosterFaces>");
		sb.append("<PlotKeywords>" + processValues(fields[16].toString().trim(), "|", "PlotKeyword") + "</PlotKeywords>");
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
		sb.append("<Name>" + fields[10].toString().trim() + "</Name>");
		sb.append("<FacebookLikes>" + fields[7].toString().trim() + "</FacebookLikes>");
		sb.append("</Actor>");
		sb.append("<Actor>");
		sb.append("<Biling>2</Biling>");
		sb.append("<Name>" + fields[6].toString().trim() + "</Name>");
		sb.append("<FacebookLikes>" + fields[24].toString().trim() + "</FacebookLikes>");
		sb.append("</Actor>");
		sb.append("<Actor>");
		sb.append("<Biling>3</Biling>");
		sb.append("<Name>" + fields[14].toString().trim() + "</Name>");
		sb.append("<FacebookLikes>" + fields[5].toString().trim() + "</FacebookLikes>");
		sb.append("</Actor>");
		sb.append("</Actors>");			
		sb.append("</Cast>");
		sb.append("</Movie>");
				
		LOG.info("Converted '" + fields[11].toString().replaceAll("\u00a0", "").replaceAll("^\"|\"$", "").trim() + "' to XML");		
		return sb.toString();
	}
	
	public static String processValues(String values, String delim, String element) {
		if (values.length() == 0) {
			return "";
		} else
		{
			String[] splitValues = values.split(Pattern.quote(delim));
			StringBuffer sb = new StringBuffer();			
			//sb.append("<" + element + ">");
			for(String value: splitValues) {
				sb.append("<"+ element + ">" +value.toString().trim() + "</"+ element + ">");
			}
			//sb.append("</" + element + ">");
			return sb.toString();
		}		
	}
	
}
