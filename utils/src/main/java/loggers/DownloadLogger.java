/**
 * 
 */
package loggers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DownloadLogger implements Processor {
    public void process(Exchange exchange) throws Exception {
    	
    	Map props = exchange.getProperties();		
    	SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
		java.util.Date dt = formatter.parse(props.get("CamelCreatedTimestamp").toString());						
		Timestamp ts = Timestamp.valueOf(LocalDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault()));
    	
        System.out.println("Received message... "        		
        		+ "exchangeID: " + exchange.getIn().getExchange().getExchangeId().toString() + "; "
        		+ "exchange pattern: " + exchange.getIn().getExchange().getPattern().toString() + "; "
        		+ "exchange timestamp: " + ts + "; "
        		+ "breadcrumbId: " + exchange.getIn().getHeader("breadcrumbId") + "; "
        		+ "JMSMessageID: " + exchange.getIn().getHeader("JMSMessageID") + "; "        		
        		+ "CamelFilePath: " + exchange.getIn().getHeader("CamelFilePath") + "; "        		
        		+ "JMSDestination: " + exchange.getIn().getHeader("JMSDestination") + "; "
  				+ "CamelFileNameProduced: " + exchange.getIn().getHeader("CamelFileNameProduced") + "; "  				
  				+ "CamelFileContentType: " + exchange.getIn().getHeader("CamelFileContentType") + "; "
  				+ "BodyType: " + exchange.getIn().getHeader("BodyType") + "; "
  				+ "Body: " + exchange.getIn().getBody().toString()  	
        );
    }
}
