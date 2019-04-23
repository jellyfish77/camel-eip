package movies;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.language.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

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
		LOG.info("URL: " + url);
		
		// call REST service and load result into POJO JSON model
		init(url);
		OmdbMovie omdbMovie= getOmdbMovie();		
		LOG.info("OmdbMovie object: " + omdbMovie.toString());
		LOG.info("Director: " + omdbMovie.getDirector());
		LOG.info("Actors: " + omdbMovie.getActors());
		
		// enrich data in XML body using POJO
		
		// return enriched body
		return xml;
	}
	
	private Client client;
	private WebTarget target;
	
	@PostConstruct
	protected void init(String url) {
		System.out.println("Init");
	    client = ClientBuilder.newClient();
	    target = client.target(url);	       
	}
	
	public OmdbMovie getOmdbMovie() {
		// HTTP GET movie from REST service and deserialize to OmdbMovie object
	    return target
	            .request(MediaType.APPLICATION_JSON)
	            .get(OmdbMovie.class);
	}
	
}
