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
		String exchangeQuery = "INSERT INTO exchanges (" + " server," + " application," + " pattern," + " exchange_id," + " created_timestamp"
				+ " ) VALUES (" + "?, ?, ?, ?, ?)";
		
		String messageQuery = "INSERT INTO messages (" + " exchange_id," + " message_id," + " direction," + 
				" payload, " + " breadcrumb_id, " + " jms_destination," + " file_path," + " file_path_produced"  
				+ " ) VALUES (" + "?, ?, ?, ?, ?, ?, ?, ?)";
		
		String headerQuery = "INSERT INTO headers (" + " message_id," + " name," + " value"
				+ " ) VALUES (" + "?, ?, ?)";
				
		Map props = exchange.getProperties();		
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");		
		java.util.Date dt = formatter.parse(props.get("CamelCreatedTimestamp").toString());						
		Timestamp ts = Timestamp.valueOf(LocalDateTime.ofInstant(dt.toInstant(), ZoneId.systemDefault()));
				
		try {
			// String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/integration?autoReconnect=true&useSSL=false";
			Class.forName("com.mysql.jdbc.Driver"); 
			Connection conn = DriverManager.getConnection(myUrl, "root", "root");
			
			//System.out.println("Executing statements as batch: ");
			
			conn.setAutoCommit(false);
			
			PreparedStatement st = conn.prepareStatement(exchangeQuery);
			st.setString(1, InetAddress.getLocalHost().getHostName());
			st.setString(2, "Apache Camel");
			st.setString(3, exchange.getIn().getExchange().getPattern().toString());
			st.setString(4, exchange.getIn().getExchange().getExchangeId().toString());
			//st.setDate(5, java.sql.Date.valueOf(dt.toString()));
			st.setTimestamp(5, ts);
			//System.out.println(st);
			st.addBatch();
			st.executeBatch();			
			
			String jms = ""; if(exchange.getIn().getHeader("JMSDestination") == null ) { jms = ""; } else { jms = exchange.getIn().getHeader("JMSDestination").toString(); }
			String fp = ""; if(exchange.getIn().getHeader("CamelFilePath") == null) { fp = ""; } else { fp = exchange.getIn().getHeader("CamelFilePath").toString(); }
			String fnp = ""; if(exchange.getIn().getHeader("CamelFileNameProduced") == null) { fnp = ""; } else { fnp = exchange.getIn().getHeader("CamelFileNameProduced").toString(); }
										
			st = conn.prepareStatement(messageQuery);
			st.setString(1, exchange.getIn().getExchange().getExchangeId());
			st.setString(2, exchange.getIn().getMessageId());
			st.setString(3, "in");
			st.setString(4, exchange.getIn().getBody(String.class));
			st.setString(5, exchange.getIn().getHeader("breadcrumbId").toString());
			st.setString(6, jms);
			st.setString(7, fp);
			st.setString(8, fnp);
			//System.out.println(st);
			st.addBatch();
			st.executeBatch();			
			
			/*
			st = conn.prepareStatement(headerQuery);
			st.setString(1, exchange.getIn().getMessageId());
			st.setString(2, "CamelFilePath");
			st.setString(3, exchange.getIn().getHeader("CamelFilePath").toString());
			//System.out.println(st);
			st.addBatch();			
			st.executeBatch();					
			
			st = conn.prepareStatement(headerQuery);
			st.setString(1, exchange.getIn().getMessageId());
			st.setString(2, "CamelFileLength");
			st.setString(3, exchange.getIn().getHeader("CamelFileLength").toString());
			//System.out.println(st);
			st.addBatch();
			st.executeBatch();			
			
			Calendar c = new GregorianCalendar();
			c.setTimeInMillis(Long.parseLong(exchange.getIn().getHeader(Exchange.FILE_LAST_MODIFIED).toString()));			
		    final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);

			st = conn.prepareStatement(headerQuery);
			st.setString(1, exchange.getIn().getMessageId());
			st.setString(2, "CamelFileLastModified");
			st.setString(3, dateFormat.format(c.getTime()));
			//System.out.println(st);
			st.addBatch();
			st.executeBatch();			
			*/
			
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