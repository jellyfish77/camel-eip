package movies;

import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovieCsvToXMLProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Logger LOG = LoggerFactory.getLogger(MovieCsvToXMLProcessor.class);
		String myString = exchange.getIn().getBody(String.class);
		String[] lineSeparator = myString.split(System.getProperty("line.separator"));
		StringBuffer sb = new StringBuffer();
		//LOG.info("TransformCsvMoviesToXml processor starting...");
		//sb.append("<Movies>");
		for (String lineData : lineSeparator) {
			String[] commaSeparator = lineData.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // handle quotes as text-delimiter
			sb.append("<Movie>");
			sb.append("<Title>" + commaSeparator[11].toString().trim().replaceAll("\u00a0", "") + "</Title>");
			sb.append("<Gross>" + commaSeparator[8].toString().trim() + "</Gross>");
			sb.append("<Genres>"  + processValues(commaSeparator[9].toString().trim(), "|", "Genre") + "</Genres>");			
			sb.append("<Color>" + commaSeparator[0].toString().trim() + "</Color>");
			sb.append("<UserVotes>" + commaSeparator[12].toString().trim() + "</UserVotes>");			
			sb.append("<NumUsersReviewed>" + commaSeparator[18].toString().trim() + "</NumUsersReviewed>");			
			sb.append("<Language>" + commaSeparator[20].toString().trim() + "</Language>");
			sb.append("<Rating>" + commaSeparator[21].toString().trim() + "</Rating>");
			sb.append("<Budget>" + commaSeparator[22].toString().trim() + "</Budget>");
			sb.append("<Year>" + commaSeparator[23].toString().trim() + "</Year>");
			sb.append("<ImdbScore>" + commaSeparator[25].toString().trim() + "</ImdbScore>");
			sb.append("<ImdbLink>" + commaSeparator[17].toString().trim() + "</ImdbLink>");
			sb.append("<AspectRatio>" + commaSeparator[26].toString().trim() + "</AspectRatio>");
			sb.append("<Country>" + commaSeparator[19].toString().trim() + "</Country>");
			sb.append("<FacebookLikes>" + commaSeparator[27].toString().trim() + "</FacebookLikes>");
			sb.append("<NumPosterFaces>" + commaSeparator[15].toString().trim() + "</NumPosterFaces>");
			sb.append("<PlotKeywords>" + processValues(commaSeparator[16].toString().trim(), "|", "PlotKeyword") + "</PlotKeywords>");
			sb.append("<Director>");
			sb.append("<Name>" + commaSeparator[1].toString().trim() + "</Name>");
			sb.append("<FacebookLikes>" + commaSeparator[4].toString().trim() + "</FacebookLikes>");			
			sb.append("</Director>");
			sb.append("<NumReviews>" + commaSeparator[2].toString().trim() + "</NumReviews>");
			sb.append("<Duration>" + commaSeparator[3].toString().trim() + "</Duration>");
			sb.append("<Cast>");
			sb.append("<Actors>");
			sb.append("<Actor>");
			sb.append("<Biling>1</Biling>");
			sb.append("<Name>" + commaSeparator[10].toString().trim() + "</Name>");
			sb.append("<FacebookLikes>" + commaSeparator[7].toString().trim() + "</FacebookLikes>");
			sb.append("</Actor>");
			sb.append("<Actor>");
			sb.append("<Biling>2</Biling>");
			sb.append("<Name>" + commaSeparator[6].toString().trim() + "</Name>");
			sb.append("<FacebookLikes>" + commaSeparator[24].toString().trim() + "</FacebookLikes>");
			sb.append("</Actor>");
			sb.append("<Actor>");
			sb.append("<Biling>3</Biling>");
			sb.append("<Name>" + commaSeparator[14].toString().trim() + "</Name>");
			sb.append("<FacebookLikes>" + commaSeparator[5].toString().trim() + "</FacebookLikes>");
			sb.append("</Actor>");
			sb.append("</Actors>");			
			sb.append("</Cast>");
			sb.append("</Movie>");
		}
		//sb.append("</Movies>");		
		//LOG.info("TransformCsvMoviesToXml processor complete");
		exchange.getIn().setBody(sb.toString());
	}
	
	public String processValues(String values, String delim, String element) {
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