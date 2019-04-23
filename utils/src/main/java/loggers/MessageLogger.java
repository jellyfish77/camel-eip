package loggers;

import java.sql.*;
//import java.util.Calendar;
import java.text.DateFormat;
//import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
//import java.time.format.DateTimeFormatter;
//import java.util;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.camel.Exchange;
import org.apache.camel.MessageHistory;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageLogger implements Processor {
	public void process(Exchange exchange) throws Exception {
		
		Logger LOG = LoggerFactory.getLogger(MessageLogger.class);
		
		String query = "INSERT INTO `integration`.`camel-message-log` ("
				+ " hostname," 
				+ " breadcrumb_id,"
				+ " exchange_id,"				
				+ " exchange_pattern,"
				+ " exchange_properties,"
				+ " route,"
				+ " camel_to_endpoint,"
				+ " message_id,"
				+ " message_direction,"
				+ " message_headers,"
				+ " message_body,"
				+ " jms_destination,"
				+ " file_path,"
				+ " file_path_produced,"
				+ " camel_timestamp" 
				+ " )"
				+ " VALUES (" + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
		Map props = exchange.getProperties();
		Map headers = exchange.getIn().getHeaders();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
		java.util.Date dt = formatter.parse(props.get("CamelCreatedTimestamp").toString());						
		Timestamp ts = Timestamp.valueOf(LocalDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault()));
		List<MessageHistory> messageHistory = exchange.getProperty(Exchange.MESSAGE_HISTORY, List.class);
		
		try {
			// String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/integration?autoReconnect=true&useSSL=false";
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection conn = DriverManager.getConnection(myUrl, "root", "root");
			
			//System.out.println("Executing statements as batch: ");
			
			String jms = ""; if(exchange.getIn().getHeader("JMSDestination") == null ) { jms = ""; } else { jms = exchange.getIn().getHeader("JMSDestination").toString(); }
			String fp = ""; if(exchange.getIn().getHeader("CamelFilePath") == null) { fp = ""; } else { fp = exchange.getIn().getHeader("CamelFilePath").toString(); }
			String fnp = ""; if(exchange.getIn().getHeader("CamelFileNameProduced") == null) { fnp = ""; } else { fnp = exchange.getIn().getHeader("CamelFileNameProduced").toString(); }
			String brcr = ""; if(exchange.getIn().getHeader("breadcrumbId").toString() == null) { brcr = ""; } else { brcr = exchange.getIn().getHeader("breadcrumbId").toString(); } 
			String ep = ""; if(props.get("CamelToEndpoint") == null) { ep = ""; } else { ep = props.get("CamelToEndpoint").toString(); }
			
			//LOG.info("Exchange property values: " + props.values().toString());			
			//LOG.info("CamelToEndpoint: " + exchange.getProperty("CamelToEndpoint").toString());			
			//LOG.info("CamelToEndpoint: " + props.get("CamelToEndpoint"));
			LOG.info("Route ID: " + messageHistory.get(messageHistory.size()-1).toString());
			
			conn.setAutoCommit(false);
						
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, InetAddress.getLocalHost().getHostName());			
			//st.setString(1, "mint-dev");
			st.setString(2, brcr);			
			st.setString(3, exchange.getIn().getExchange().getExchangeId().toString());
			st.setString(4, exchange.getIn().getExchange().getPattern().toString());
			st.setString(5, props.toString());
			st.setString(6, messageHistory.get(messageHistory.size()-1).toString());
			st.setString(7,	ep);
			st.setString(8, exchange.getIn().getMessageId());			
			st.setString(9, "In");
			st.setString(10, headers.toString());
			st.setString(11, exchange.getIn().getBody(String.class));
			st.setString(12, jms);
			st.setString(13, fp);
			st.setString(14, fnp);
			st.setTimestamp(15, ts);
			
			//System.out.println(st);
			st.addBatch();
			st.executeBatch();			
					
			conn.commit();
			st.close();
			conn.close();
		} catch (UnknownHostException e) {		 
            e.printStackTrace();            
		} catch (SQLException se) {
			// log exception
			throw se;
		}
	}
}