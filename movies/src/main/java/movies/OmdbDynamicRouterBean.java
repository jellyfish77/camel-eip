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
		Logger LOG = LoggerFactory.getLogger(ConvertCsvToXml.class);	
		
		LOG.info("Previous route:" + previous);
		
		if (previous == null) {
			return "mock://a";
		} else if ("mock://a".equals(previous)) {
			return "language://simple:Bye ${body}";
		} else {
			return null;
		}
	}

}
