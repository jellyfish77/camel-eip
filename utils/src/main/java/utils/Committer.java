package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.regex.*;

public class Committer {

	public Document commit(@Body Document xml, String driverClass, String connStr, String user, String password,
			String database, String table) throws Exception {

		Logger LOG = LoggerFactory.getLogger(Committer.class);

		//String query = "INSERT INTO `" + database + "`.`" + table + "`";
		String query = "INSERT INTO `" + table + "`";

		//LOG.info("Invoked with: " + driverClass.replace("[", "").replace("]", "") + ", " + connStr + ", " + user + ", " + password);
		LOG.info("Comitting movie '" + xml.getElementsByTagName("Field").item(0).getTextContent() + "'....");

		// generate SQL statement
		NodeList nodeList = xml.getElementsByTagName("Field");

		String placeHolders = genPlaceholderList(nodeList.getLength());
		String fields = genFieldList(nodeList);

		query = query + " (" + fields + ") VALUES (" + placeHolders + ");";
		//LOG.info(query);

		// commit statement
		try {
			String myUrl = connStr.replaceAll("<database>", database);
			//LOG.info("Conn: " + myUrl);
			Class.forName(driverClass.replace("[", "").replace("]", ""));
			Connection conn = DriverManager.getConnection(myUrl, "root", "root");

			conn.setAutoCommit(false);

			PreparedStatement st = conn.prepareStatement(query);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {					
					String value = node.getTextContent();
					
					// if in DATE format remove trailing 'Z' (UTC indicator)					
					if(Pattern.matches("^(\\d{4})-(\\d{2})-(\\d{2})Z$", value)) {
						value = charRemoveAt(value, value.length()-1); 
					}
					
					st.setString(i+1, value);
				}
			}
			
			LOG.info(st.toString());

			st.addBatch();
			st.executeBatch();
			conn.commit();
			st.close();
			conn.close();
		} catch (SQLException se) {
			LOG.error("Failed committing data to target! [Commit Failed]");
			throw se;
		} catch (ClassNotFoundException e) {
			LOG.error("Failed to load driver class! [Commit Failed]");
			throw e;
		} catch (Exception e) {
			LOG.error("Something went wrong during commit process! [Commit Failed]");
			throw e;
		}

		LOG.info("Finished comitting movie '" + xml.getElementsByTagName("Field").item(0).getTextContent() + "'");

		return xml;
	}

	private String genFieldList(NodeList nodeList) {
		String fields = "";

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				// LOG.info(e.getAttribute("name") + ": " + node.getTextContent());
				fields = fields + e.getAttribute("name") + ", ";
			}
		}

		return fields.substring(0, fields.length() - 2);
	}

	private String genPlaceholderList(int num) {
		String plsHldrs = "";

		for (int i = 0; i < num; i++) {
			plsHldrs = plsHldrs + "?, ";
		}

		return plsHldrs.substring(0, plsHldrs.length() - 2);
	}

	private String charRemoveAt(String str, int p) {
		return str.substring(0, p) + str.substring(p + 1);
	}
}
