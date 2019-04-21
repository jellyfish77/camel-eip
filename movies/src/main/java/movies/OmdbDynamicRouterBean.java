package movies;

import org.apache.camel.DynamicRouter;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmdbDynamicRouterBean {

	@DynamicRouter
	public String route(String body, @Header(Exchange.SLIP_ENDPOINT) String previous) {
		return whereToGo(body, previous);
	}

	private String whereToGo(String body, String previous) {
		Logger LOG = LoggerFactory.getLogger(OmdbDynamicRouterBean.class);	
		
		LOG.info("Previous route:" + previous);
		
		if (previous == null) {
			return "http://www.omdbapi.com/?apikey=9949578d&amp;t=The+Chronicles+of+Narnia%3A+The+Lion%2C+the+Witch+and+the+Wardrobe";		
		} else {
			return null;
		}
	}

}
