package movies;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class OmdbMovieMapper {

	public static OmdbMovie createObdmMovie(String jsonData) throws JsonParseException, JsonMappingException, IOException {

		// create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();

		// convert json string to object
		//OmdbMovie omdbMovie = objectMapper.readValue(jsonData, OmdbMovie.class);
		OmdbMovie omdbMovie = objectMapper.reader().forType(OmdbMovie.class).readValue(jsonData);
	
		return omdbMovie;
	}
}
