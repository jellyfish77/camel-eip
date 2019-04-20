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
import java.util.Map;
//import java.time.format.DateTimeFormatter;
//import java.util;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MessageLogger implements Processor {
	public void process(Exchange exchange) throws Exception {
		
		String query = "INSERT INTO `integration`.`camel-message-log` ("
				+ " hostname," 
				+ " breadcrumb_id,"
				+ " exchange_id,"				
				+ " exchange_pattern,"
				+ " exchange_properties,"
				+ " message_id,"
				+ " message_direction,"
				+ " message_headers,"
				+ " message_body,"
				+ " jms_destination,"
				+ " file_path,"
				+ " file_path_produced,"
				+ " camel_timestamp" 
				+ " )"
				+ " VALUES (" + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
		Map props = exchange.getProperties();
		Map headers = exchange.getIn().getHeaders();
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
		java.util.Date dt = formatter.parse(props.get("CamelCreatedTimestamp").toString());						
		Timestamp ts = Timestamp.valueOf(LocalDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault()));
				
		try {
			// String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/integration?autoReconnect=true&useSSL=false";
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection conn = DriverManager.getConnection(myUrl, "root", "root");
			
			//System.out.println("Executing statements as batch: ");
			
			String jms = ""; if(exchange.getIn().getHeader("JMSDestination") == null ) { jms = ""; } else { jms = exchange.getIn().getHeader("JMSDestination").toString(); }
			String fp = ""; if(exchange.getIn().getHeader("CamelFilePath") == null) { fp = ""; } else { fp = exchange.getIn().getHeader("CamelFilePath").toString(); }
			String fnp = ""; if(exchange.getIn().getHeader("CamelFileNameProduced") == null) { fnp = ""; } else { fnp = exchange.getIn().getHeader("CamelFileNameProduced").toString(); }
						
			conn.setAutoCommit(false);
			
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, InetAddress.getLocalHost().getHostName());			
			st.setString(2, exchange.getIn().getHeader("breadcrumbId").toString());			
			st.setString(3, exchange.getIn().getExchange().getExchangeId().toString());
			st.setString(4, exchange.getIn().getExchange().getPattern().toString());
			st.setString(5, props.toString());
			st.setString(6, exchange.getIn().getMessageId());			
			st.setString(7, "In");
			st.setString(8, headers.toString());
			st.setString(9, exchange.getIn().getBody(String.class));
			st.setString(10, jms);
			st.setString(11, fp);
			st.setString(12, fnp);
			st.setTimestamp(13, ts);
			
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