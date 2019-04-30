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
		String title = ((node.get("Title").asText()==null) ? "" : node.get("Title").asText());
		//int year = (Integer) ((IntNode) node.get("year")).numberValue();
		String year =  node.get("Year").asText();
		String rated =  node.get("Rated").asText();
		String released =  node.get("Released").asText();
		String runtime=  node.get("Runtime").asText();
		String genre =  node.get("Genre").asText();
		String director=  node.get("Director").asText();
		String writer =  node.get("Writer").asText();
		String actors =  node.get("Actors").asText();
		String plot =  node.get("Plot").asText();
		String language =  node.get("Language").asText();		
		String country =  node.get("Country").asText();
		String awards =  node.get("Awards").asText();
		String poster =  node.get("Poster").asText();
		ArrayList<OmdbRating> ratings = new ArrayList<OmdbRating>();		
		//ratings.add(new OmdbRating("goober", "10"));
		//ratings = new ObjectMapper().readValue(node.get("Ratings").asText(), new TypeReference<ArrayList<OmdbRating>>() {});
		JsonNode locatedNode = node.path("Ratings");
		//LOG.info(locatedNode.toString());		
		if (locatedNode.isArray()) {
		    for (final JsonNode objNode : locatedNode) {
		        //LOG.info(objNode.toString());
		        ratings.add(new OmdbRating(objNode.get("Source").asText(), objNode.get("Value").asText()));
		    }
		}		
		String metascore =  node.get("Metascore").asText();
		String imdbRating =  node.get("imdbRating").asText().toString();
		String imdbVotes =  node.get("imdbVotes").asText();
		String imdbId =  node.get("imdbID").asText();
		String type =  node.get("Type").asText();
		String dvd =  node.get("DVD").asText();
		String boxOffice =  node.get("BoxOffice").asText();
		String production=  node.get("Production").asText();
		String website =  node.get("Website").asText();
		String response =  node.get("Response").asText();
		
		OmdbMovie omdbMovie = new OmdbMovie();
		omdbMovie.setTitle(title);
		omdbMovie.setYear(year);
		
		return new OmdbMovie(title, year, rated, released, runtime, genre, director, writer, actors, plot, language, country, awards, poster, ratings, metascore, imdbRating, imdbVotes, imdbId, type, dvd, boxOffice, 
				production, website, response);
		//return omdbMovie;
	}
}