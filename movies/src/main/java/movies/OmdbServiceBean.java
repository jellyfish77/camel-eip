package movies;

import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.language.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

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
		
		
		// enrich data in XML body using POJO
		
		// return enriched body
		return xml;
	}
}
