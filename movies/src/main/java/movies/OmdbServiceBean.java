package movies;

import org.apache.camel.Body;
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

	
	public Document enrichMovie(@Body Document xml, @XPath("/Movie/Title/text()") String movieTitle) {
		
		Logger LOG = LoggerFactory.getLogger(OmdbServiceBean.class);
		
		LOG.info("Movie title '" + movieTitle + "'");
		
		// get movie title
		
		// uri encode title and build url
		
		// call REST service and load result into POJO JSON model
		
		// enrich data in XML body using POJO
		
		// return enriched body
		return xml;
	}
}
