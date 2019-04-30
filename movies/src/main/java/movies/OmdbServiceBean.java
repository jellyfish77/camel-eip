package movies;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.language.XPath;
import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.ws.rs.client.ClientBuilder;

/*
 * Facilitates the invocation of the OMDB REST Service and enrichment of the
 * movie XML payload.
 * 
 */
public class OmdbServiceBean {

	private Object nodeList;

	// Enrich XML with addtional data from JSON web service query
	public Document enrichMovie(@Headers Map<String, Object> headers, @Body Document xml,
			@XPath("/Movie/Title/text()") String movieTitle) {

		Logger LOG = LoggerFactory.getLogger(OmdbServiceBean.class);
		OmdbMovie omdbMovie = null;

		////////////////////////////////////////////////////////////////////////
		// Query OMDB web service using movie title
		////////////////////////////////////////////////////////////////////////

		// uri encode title and build url
		// (http://www.omdbapi.com/?apikey=<key>&t=The+Crying+Game)
		movieTitle = webservices.UniversalResourceIdentifer.encodeURIComponent(movieTitle);
		// LOG.info("Movie title '" + movieTitle + "'");
		// LOG.info("Headers: " + headers.toString());
		// LOG.info("URL: " + headers.get("omdbUrl"));
		String url = headers.get("OmdbUrl") + movieTitle;
		url = url.replaceAll("<key>", headers.get("OmdbKey").toString());
		LOG.info("Enriching XML Movie Record using REST Service at URL \"" + url + "\"");

		// call REST service and save resultant JSON
		String json = ClientBuilder.newClient().target(url).request().accept(MediaType.APPLICATION_JSON)
				.get(String.class);
		LOG.info("HTTP GET Resp:" + json);

		if (json.contains("Movie not found!")) { // if no data found
			LOG.info("No data available for '" + movieTitle + "'");
			// create empty object
			omdbMovie = new OmdbMovie();
		} else {
			// deserialize JSON to object
			try {
				omdbMovie = OmdbMovieMapper.createObdmMovie(json);
			} catch (IOException e) {
				System.out.println("Uh oh spaghettios!");
				e.printStackTrace();
			}
		}

		////////////////////////////////////////////////////////////////////////
		// Enrich data in XML body with data from OMDB web service
		////////////////////////////////////////////////////////////////////////

		Node movieNode = xml.getFirstChild();

		// append imdb id
		Element imdbElement = xml.createElement("ImdbId");
		imdbElement.appendChild(xml.createTextNode(omdbMovie.getImdbID()));
		movieNode.insertBefore(imdbElement, (Element) xml.getElementsByTagName("Gross").item(0));

		// append production info node
		Element productionElement = xml.createElement("Production");
		Element countriesElement = xml.createElement("Countries");
		try {
			if (omdbMovie.getCountry() != "") {
				List<String> countriesList = Arrays.asList(omdbMovie.getCountry().split("\\s*,\\s*"));
				// LOG.info("Found " + countriesList.size() + " countries");
				for (String countryStr : countriesList) {
					Element countryElement = xml.createElement("Country");
					countryElement.appendChild(xml.createTextNode(countryStr));
					countriesElement.appendChild(countryElement);
				}				
			} else {
				LOG.info("No countries found for movie +'" + movieTitle + "'");
			}
		} catch (NullPointerException npe) {
		}
		productionElement.appendChild(countriesElement);		
		productionElement.appendChild((Element) xml.getElementsByTagName("Language").item(0));
		productionElement.appendChild((Element) xml.getElementsByTagName("Budget").item(0)); //
		productionElement.appendChild((Element) xml.getElementsByTagName("Year").item(0));
		Element dateElement = xml.createElement("ReleaseDate");
		dateElement.appendChild(xml.createTextNode(omdbMovie.getReleased()));
		productionElement.appendChild(dateElement);
		Element dvdElement = xml.createElement("DvdDate");
		dvdElement.appendChild(xml.createTextNode(omdbMovie.getDVD()));
		productionElement.appendChild(dvdElement);
		Element websiteElement = xml.createElement("Website");
		websiteElement.appendChild(xml.createTextNode(omdbMovie.getWebsite()));
		productionElement.appendChild(websiteElement);
		movieNode.insertBefore(productionElement, (Element) xml.getElementsByTagName("Genres").item(0));

		// append format info node
		Element formatElement = xml.createElement("Format");
		Element typeElement = xml.createElement("Type");
		typeElement.appendChild(xml.createTextNode(omdbMovie.getType()));
		formatElement.appendChild(typeElement);
		formatElement.appendChild((Element) xml.getElementsByTagName("Color").item(0));
		formatElement.appendChild((Element) xml.getElementsByTagName("Language").item(0));
		formatElement.appendChild((Element) xml.getElementsByTagName("AspectRatio").item(0));		
		Element runElement = xml.createElement("Runtime");
		if (omdbMovie.getRuntime() != "") {
			LOG.info("Setting Runtime to JSON Runtime");			
			String runtimes[] = omdbMovie.getRuntime().split(" ", 2);
			runElement.appendChild(xml.createTextNode(runtimes[0]));
		} else {			
			String dur = xml.getElementsByTagName("Duration").item(0).getTextContent();
			LOG.info("Setting Runtime to Duration: " + dur);			
			runElement.appendChild(xml.createTextNode(dur));
		}
		Element durElement = (Element) xml.getElementsByTagName("Duration").item(0);
		durElement.getParentNode().removeChild(durElement);
		formatElement.appendChild(runElement);
		movieNode.insertBefore(formatElement, (Element) xml.getElementsByTagName("Production").item(0));

		// append plot node
		Element plotElement = xml.createElement("Plot");
		Element plotDescElement = xml.createElement("Description");
		plotDescElement.appendChild(xml.createTextNode(omdbMovie.getPlot()));
		plotElement.appendChild(plotDescElement);
		plotElement.appendChild((Element) xml.getElementsByTagName("PlotKeywords").item(0));
		movieNode.insertBefore(plotElement, (Element) xml.getElementsByTagName("UserVotes").item(0));

		// append awards node
		Element awardsElement = xml.createElement("Awards");
		awardsElement.appendChild(xml.createTextNode(omdbMovie.getAwards()));
		movieNode.insertBefore(awardsElement, (Element) xml.getElementsByTagName("UserVotes").item(0));

		// append poster node
		Element posterElement = xml.createElement("Poster");
		posterElement.appendChild(xml.createTextNode(omdbMovie.getPoster()));
		movieNode.insertBefore(posterElement, (Element) xml.getElementsByTagName("Awards").item(0));

		// remove IMDB score node
		Element scoreElement = (Element) xml.getElementsByTagName("ImdbScore").item(0);
		movieNode.removeChild(scoreElement);

		// append writers
		Element writersElement = xml.createElement("Writers");
		if (omdbMovie.getWriter() != "") {
			Element writerElement = xml.createElement("Writer");
			writerElement.appendChild(xml.createTextNode(omdbMovie.getWriter()));
			writersElement.appendChild(writerElement);
		}
		movieNode.insertBefore(writersElement, (Element) xml.getElementsByTagName("NumReviews").item(0));
				
		// append ratings
		Element ratingsElement = xml.createElement("Ratings");
		try {
			for (OmdbRating rating : omdbMovie.getRatings()) {
				Element ratingElement = xml.createElement("Rating");
				Element sourceElement = xml.createElement("Source");
				Element valueElement = xml.createElement("Value");
				sourceElement.appendChild(xml.createTextNode(rating.getSource()));
				valueElement.appendChild(xml.createTextNode(rating.getValue()));
				ratingElement.appendChild(sourceElement);
				ratingElement.appendChild(valueElement);
				ratingsElement.appendChild(ratingElement);
				
			}
		} catch (NullPointerException npe) {
			LOG.info("No ratings exist for movie '" + movieTitle + "'");
		}
		//movieNode.insertBefore(ratingsElement, (Element) xml.getElementsByTagName("Duration").item(0));
		movieNode.appendChild(ratingsElement);
		
		// append actors (if not already present)
		try {
			if (omdbMovie.getActors() != "") {
				List<String> actorsList = Arrays.asList(omdbMovie.getActors().split("\\s*,\\s*"));
				javax.xml.xpath.XPath xPath = XPathFactory.newInstance().newXPath();
				// LOG.info("Found " + actorsList.size() + " writer(s)");
				for (String actorStr : actorsList) {
					try {
						LOG.info("Checking if actor '" + actorStr + "' exists...");
						LOG.info("Using xpath: " + "//Actor/Name[text()='" + utils.Encoder.escapeXmlChars(actorStr)
								+ "']");
						NodeList nodeList = (NodeList) xPath
								.compile("//Actor/Name[text()='" + utils.Encoder.escapeXmlChars(actorStr) + "']")
								.evaluate(xml, XPathConstants.NODESET);
						LOG.info("Actor '" + actorStr + "' already exists " + nodeList.getLength()
								+ " times in xml doc");
						// add actor node if the actor not already in xml doc
						if (nodeList.getLength() == 0) {
							Element actorElement = xml.createElement("Actor");
							Element nameElement = xml.createElement("Name");
							actorElement.appendChild(nameElement);
							nameElement.appendChild(xml.createTextNode(actorStr));
							NodeList actorsNodes = xml.getElementsByTagName("Actors");
							actorsNodes.item(0).appendChild(actorElement);
						}
					} catch (XPathExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				LOG.info("No actors found for movie '" + movieTitle + "'");
			}
		} catch (NullPointerException npe) {
		}

		// return enriched body
		return xml;
	}

}
