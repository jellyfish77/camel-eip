package movies;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.language.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.ClientBuilder;

/*
 * Facilitates the invocation of the OMDB REST Service and enrichment of the
 * movie XML payload.
 * 
 */
public class OmdbServiceBean {

	public Document enrichMovie(@Headers Map<String, Object> headers, @Body Document xml, @XPath("/Movie/Title/text()") String movieTitle) {
		
		Logger LOG = LoggerFactory.getLogger(OmdbServiceBean.class);
		
		// uri encode title and build url (http://www.omdbapi.com/?apikey=<key>&t=The+Crying+Game)		
		movieTitle = webservices.UniversalResourceIdentifer.encodeURIComponent(movieTitle);
		//LOG.info("Movie title '" + movieTitle + "'");
		//LOG.info("Headers: " + headers.toString());
		//LOG.info("URL: " + headers.get("omdbUrl"));
		String url = headers.get("omdbUrl") + movieTitle;
		url = url.replaceAll("<key>", headers.get("omdbKey").toString());
		LOG.info("Enriching XML Movie Record using REST Service at URL \"" + url + "\"");
		
		// call REST service and save resultant JSON 
		String json = ClientBuilder.newClient().target(url).request().accept(MediaType.APPLICATION_JSON).get(String.class);
		//LOG.info("HTTP GET Resp:" + json);
		
		// deserialize JSON to object
		OmdbMovie omdbMovie = null;		
		try {
			omdbMovie = OmdbMovieMapper.createObdmMovie(json);
		} catch (IOException e) {			
			System.out.println("Uh oh spaghettios!");
			e.printStackTrace();
		}		
		//LOG.info("OmdbMovie Object: " + omdbMovie.toString());
		
		for (OmdbRating rating : omdbMovie.getRatings()) {
			LOG.info("Rating Source: " + rating.getSource() + ", Rating Value: " + rating.getValue()); 
		}
				
		// enrich data in XML body with data from OMDB REST service
		Node movieNode = xml.getFirstChild();
		
		// append writer
		Element writersElement = xml.createElement("Writers");
		Element writerElement = xml.createElement("Writer");
		writerElement.appendChild(xml.createTextNode(omdbMovie.getWriter()));
		writersElement.appendChild(writerElement);
		movieNode.insertBefore(writersElement, (Element) xml.getElementsByTagName("NumReviews").item(0));
				
		// append ratings
		Element ratingsElement = xml.createElement("Ratings");		
		for (OmdbRating rating : omdbMovie.getRatings()) {
			Element ratingElement = xml.createElement("Rating");
			Element sourceElement = xml.createElement("Source");
			Element valueElement = xml.createElement("Value");			
			sourceElement.appendChild(xml.createTextNode(rating.getSource()));
			valueElement.appendChild(xml.createTextNode(rating.getValue()));
			ratingElement.appendChild(sourceElement);
			ratingElement.appendChild(valueElement);
			ratingsElement.appendChild(ratingElement);
			movieNode.insertBefore(ratingsElement, (Element) xml.getElementsByTagName("Duration").item(0));
		}
		
		// return enriched body		
		return xml;
	}
	
}
