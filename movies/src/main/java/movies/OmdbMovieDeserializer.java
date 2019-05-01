package movies;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

public class OmdbMovieDeserializer extends StdDeserializer<OmdbMovie> {

	Logger LOG = LoggerFactory.getLogger(OmdbMovieDeserializer.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OmdbMovieDeserializer() { 
        this(null); 
    }

	public OmdbMovieDeserializer(Class<?> vc) { 
        super(vc); 
    }

	@Override
	public OmdbMovie deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		//int id = (Integer) ((IntNode) node.get("id")).numberValue();
		//String title = ((node.get("Title").asText()==null) ? "" : node.get("Title").asText());
		String title = getNodeValAsText(node, "Title");
		//int year = (Integer) ((IntNode) node.get("year")).numberValue();
		String year =  getNodeValAsText(node, "Year");
		String rated =  getNodeValAsText(node, "Rated");
		String released =  getNodeValAsText(node, "Released");
		String runtime=  getNodeValAsText(node, "Runtime");
		String genre =  getNodeValAsText(node, "Genre");
		String director=  getNodeValAsText(node, "Director");
		String writer =  getNodeValAsText(node, "Writer");
		String actors =  getNodeValAsText(node, "Actors");
		String plot =  getNodeValAsText(node, "Plot");
		String language =  getNodeValAsText(node, "Language");		
		String country =  getNodeValAsText(node, "Country");
		String awards =  getNodeValAsText(node, "Awards");
		String poster =  getNodeValAsText(node, "Poster");
		ArrayList<OmdbRating> ratings = new ArrayList<OmdbRating>();		
		//ratings.add(new OmdbRating("goober", "10"));
		//ratings = new ObjectMapper().readValue(node.get("Ratings").asText(), new TypeReference<ArrayList<OmdbRating>>() {});
		JsonNode locatedNode = node.path("Ratings");
		//LOG.info(locatedNode.toString());		
		if (locatedNode.isArray()) {
		    for (final JsonNode objNode : locatedNode) {
		        //LOG.info(objNode.toString());
		        ratings.add(new OmdbRating(getNodeValAsText(objNode, "Source"), getNodeValAsText(objNode, "Value")));
		    }
		}		
		String metascore =  getNodeValAsText(node, "Metascore");
		String imdbRating =  getNodeValAsText(node, "imdbRating");
		String imdbVotes =  getNodeValAsText(node, "imdbVotes");
		String imdbId =  getNodeValAsText(node, "imdbID");
		String type =  getNodeValAsText(node, "Type");
		String dvd =  getNodeValAsText(node, "DVD");
		String boxOffice =  getNodeValAsText(node, "BoxOffice");
		String production=  getNodeValAsText(node, "Production");
		String website =  getNodeValAsText(node, "Website");
		String response =  getNodeValAsText(node, "Response");
		
		OmdbMovie omdbMovie = new OmdbMovie();
		omdbMovie.setTitle(title);
		omdbMovie.setYear(year);
		
		return new OmdbMovie(title, year, rated, released, runtime, genre, director, writer, actors, plot, language, country, awards, poster, ratings, metascore, imdbRating, imdbVotes, imdbId, type, dvd, boxOffice, 
				production, website, response);
		//return omdbMovie;
	}
	
	private String getNodeValAsText(JsonNode node, String nodeName) {
		try {
			return ((node.get(nodeName).asText()==null) ? "" : node.get(nodeName).asText());
		} catch (NullPointerException npe)
		{
			LOG.warn("Couln't find JSON node '" + nodeName + "', returning empty string");
			return "";
		}
		
	}
}