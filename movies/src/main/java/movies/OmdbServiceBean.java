package movies;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.language.XPath;
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

	public Document enrichMovie(@Headers Map<String, Object> headers, @Body Document xml,
			@XPath("/Movie/Title/text()") String movieTitle) {

		Logger LOG = LoggerFactory.getLogger(OmdbServiceBean.class);

		// uri encode title and build url
		// (http://www.omdbapi.com/?apikey=<key>&t=The+Crying+Game)
		movieTitle = webservices.UniversalResourceIdentifer.encodeURIComponent(movieTitle);
		// LOG.info("Movie title '" + movieTitle + "'");
		// LOG.info("Headers: " + headers.toString());
		// LOG.info("URL: " + headers.get("omdbUrl"));
		String url = headers.get("omdbUrl") + movieTitle;
		url = url.replaceAll("<key>", headers.get("omdbKey").toString());
		LOG.info("Enriching XML Movie Record using REST Service at URL \"" + url + "\"");

		// call REST service and save resultant JSON
		String json = ClientBuilder.newClient().target(url).request().accept(MediaType.APPLICATION_JSON)
				.get(String.class);
		LOG.info("HTTP GET Resp:" + json);

		// deserialize JSON to object
		OmdbMovie omdbMovie = null;
		try {
			omdbMovie = OmdbMovieMapper.createObdmMovie(json);
		} catch (IOException e) {
			System.out.println("Uh oh spaghettios!");
			e.printStackTrace();
		}
		// LOG.info("OmdbMovie Object: " + omdbMovie.toString());

		for (OmdbRating rating : omdbMovie.getRatings()) {
			LOG.info("Rating Source: " + rating.getSource() + ", Rating Value: " + rating.getValue());
		}

		// enrich data in XML body with data from OMDB REST service
		Node movieNode = xml.getFirstChild();
		javax.xml.xpath.XPath xPath = XPathFactory.newInstance().newXPath();
		
		// append imdb id
		Element imdbElement = xml.createElement("ImdbId");
		imdbElement.appendChild(xml.createTextNode(omdbMovie.getImdbID()));
		movieNode.insertBefore(imdbElement, (Element) xml.getElementsByTagName("Gross").item(0));
		
		// append production info node
		Element productionElement = xml.createElement("Production");		
		//productionElement.appendChild((Element) xml.getElementsByTagName("Country").item(0));
		
		List<String> countriesList = Arrays.asList(omdbMovie.getCountry().split("\\s*,\\s*"));
		LOG.info("Found " + countriesList.size() + " countries");		
		Element countriesElement = xml.createElement("Countries");
		for (String countryStr : countriesList) {						
			Element countryElement = xml.createElement("Country");
			countryElement.appendChild(xml.createTextNode(countryStr));
			countriesElement.appendChild(countryElement);
		}
		productionElement.appendChild(countriesElement);		
		
		productionElement.appendChild((Element) xml.getElementsByTagName("Language").item(0));		
		productionElement.appendChild((Element) xml.getElementsByTagName("Budget").item(0));		//
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
		Element durElement = (Element) xml.getElementsByTagName("Duration").item(0);		 
        durElement.getParentNode().removeChild(durElement);
		Element runElement = xml.createElement("Runtime");
		runElement.appendChild(xml.createTextNode(omdbMovie.getRuntime()));
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
		Element writerElement = xml.createElement("Writer");
		writerElement.appendChild(xml.createTextNode(omdbMovie.getWriter()));
		writersElement.appendChild(writerElement);
		movieNode.insertBefore(writersElement, (Element) xml.getElementsByTagName("NumReviews").item(0));

		// append ratings
		Element ratingsElement = xml.createElement("Ratings");
		for (OmdbRating rating : omdbMovie.getRatings()) {
			Element ratingElement = xml.createElement("Rating");
			Element sourceElement = xml.createElement("Source");
			Element valueElement = xml.createElement("Value");
			sourceElement.appendChild(xml.createTextNode(rating.getSource()));
			valueElement.appendChild(xml.createTextNode(rating.getValue()));
			ratingElement.appendChild(sourceElement);
			ratingElement.appendChild(valueElement);
			ratingsElement.appendChild(ratingElement);
			movieNode.insertBefore(ratingsElement, (Element) xml.getElementsByTagName("Duration").item(0));
		}

		// append actors (if not already present)
		List<String> actorsList = Arrays.asList(omdbMovie.getActors().split("\\s*,\\s*"));
		LOG.info("Found " + actorsList.size() + " writer(s)");
		//javax.xml.xpath.XPath xPath = XPathFactory.newInstance().newXPath();
		for (String actorStr : actorsList) {
			try {
				NodeList nodeList = (NodeList) xPath.compile("//Actor/Name[text()='" + actorStr + "']").evaluate(xml,
						XPathConstants.NODESET);
				LOG.info("Matches for '" + actorStr + "': " + nodeList.getLength());
				if (nodeList.getLength() == 0) { 		// actor not yet in XML doc
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
		// return enriched body
		return xml;
	}

}
