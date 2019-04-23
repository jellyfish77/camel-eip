package movies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import org.apache.commons.lang.builder.ToStringBuilder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Title", "Year", "Rated", "Released", "Runtime", "Genre", "Director", "Writer", "Actors", "Plot",
		"Language", "Country", "Awards", "Poster", "Ratings", "Metascore", "imdbRating", "imdbVotes", "imdbID", "Type",
		"DVD", "BoxOffice", "Production", "Website", "Response" })
@JsonDeserialize(using = OmdbMovieDeserializer.class)
public class OmdbMovie {

	@JsonProperty("Title")
	private String title;
	@JsonProperty("Year")
	private String year;
	@JsonProperty("Rated")
	private String rated;
	@JsonProperty("Released")
	private String released;
	@JsonProperty("Runtime")
	private String runtime;
	@JsonProperty("Genre")
	private String genre;
	@JsonProperty("Director")
	private String director;
	@JsonProperty("Writer")
	private String writer;
	@JsonProperty("Actors")
	private String actors;
	@JsonProperty("Plot")
	private String plot;
	@JsonProperty("Language")
	private String language;
	@JsonProperty("Country")
	private String country;
	@JsonProperty("Awards")
	private String awards;
	@JsonProperty("Poster")
	private String poster;
	@JsonProperty("Ratings")
	private List<OmdbRating> ratings = null;
	@JsonProperty("Metascore")
	private String metascore;
	@JsonProperty("imdbRating")
	private String imdbRating;
	@JsonProperty("imdbVotes")
	private String imdbVotes;
	@JsonProperty("imdbID")
	private String imdbID;
	@JsonProperty("Type")
	private String type;
	@JsonProperty("DVD")
	private String dVD;
	@JsonProperty("BoxOffice")
	private String boxOffice;
	@JsonProperty("Production")
	private String production;
	@JsonProperty("Website")
	private String website;
	@JsonProperty("Response")
	private String response;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public OmdbMovie() {
	}

	/**
	 * 
	 * @param genre
	 * @param metascore
	 * @param imdbVotes
	 * @param runtime
	 * @param imdbID
	 * @param type
	 * @param production
	 * @param plot
	 * @param response
	 * @param released
	 * @param imdbRating
	 * @param title
	 * @param actors
	 * @param year
	 * @param writer
	 * @param boxOffice
	 * @param website
	 * @param director
	 * @param dVD
	 * @param country
	 * @param awards
	 * @param poster
	 * @param language
	 * @param rated
	 * @param ratings
	 * @return
	 */
	public OmdbMovie(String title, String year, String rated, String released, String runtime, String genre,
			String director, String writer, String actors, String plot, String language, String country, String awards,
			String poster, List<OmdbRating> ratings, String metascore, String imdbRating, String imdbVotes,
			String imdbID, String type, String dVD, String boxOffice, String production, String website,
			String response) {
//super();
		this.title = title;
		this.year = year;
		this.rated = rated;
		this.released = released;
		this.runtime = runtime;
		this.genre = genre;
		this.director = director;
		this.writer = writer;
		this.actors = actors;
		this.plot = plot;
		this.language = language;
		this.country = country;
		this.awards = awards;
		this.poster = poster;
		this.ratings = ratings;
		this.metascore = metascore;
		this.imdbRating = imdbRating;
		this.imdbVotes = imdbVotes;
		this.imdbID = imdbID;
		this.type = type;
		this.dVD = dVD;
		this.boxOffice = boxOffice;
		this.production = production;
		this.website = website;
		this.response = response;
	}

	@JsonProperty("Title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("Title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("Year")
	public String getYear() {
		return year;
	}

	@JsonProperty("Year")
	public void setYear(String year) {
		this.year = year;
	}

	@JsonProperty("Rated")
	public String getRated() {
		return rated;
	}

	@JsonProperty("Rated")
	public void setRated(String rated) {
		this.rated = rated;
	}

	@JsonProperty("Released")
	public String getReleased() {
		return released;
	}

	@JsonProperty("Released")
	public void setReleased(String released) {
		this.released = released;
	}

	@JsonProperty("Runtime")
	public String getRuntime() {
		return runtime;
	}

	@JsonProperty("Runtime")
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	@JsonProperty("Genre")
	public String getGenre() {
		return genre;
	}

	@JsonProperty("Genre")
	public void setGenre(String genre) {
		this.genre = genre;
	}

	@JsonProperty("Director")
	public String getDirector() {
		return director;
	}

	@JsonProperty("Director")
	public void setDirector(String director) {
		this.director = director;
	}

	@JsonProperty("Writer")
	public String getWriter() {
		return writer;
	}

	@JsonProperty("Writer")
	public void setWriter(String writer) {
		this.writer = writer;
	}

	@JsonProperty("Actors")
	public String getActors() {
		return actors;
	}

	@JsonProperty("Actors")
	public void setActors(String actors) {
		this.actors = actors;
	}

	@JsonProperty("Plot")
	public String getPlot() {
		return plot;
	}

	@JsonProperty("Plot")
	public void setPlot(String plot) {
		this.plot = plot;
	}

	@JsonProperty("Language")
	public String getLanguage() {
		return language;
	}

	@JsonProperty("Language")
	public void setLanguage(String language) {
		this.language = language;
	}

	@JsonProperty("Country")
	public String getCountry() {
		return country;
	}

	@JsonProperty("Country")
	public void setCountry(String country) {
		this.country = country;
	}

	@JsonProperty("Awards")
	public String getAwards() {
		return awards;
	}

	@JsonProperty("Awards")
	public void setAwards(String awards) {
		this.awards = awards;
	}

	@JsonProperty("Poster")
	public String getPoster() {
		return poster;
	}

	@JsonProperty("Poster")
	public void setPoster(String poster) {
		this.poster = poster;
	}

	@JsonProperty("Ratings")
	public List<OmdbRating> getRatings() {
		return ratings;
	}

	@JsonProperty("Ratings")
	public void setRatings(List<OmdbRating> ratings) {
		this.ratings = ratings;
	}

	@JsonProperty("Metascore")
	public String getMetascore() {
		return metascore;
	}

	@JsonProperty("Metascore")
	public void setMetascore(String metascore) {
		this.metascore = metascore;
	}

	@JsonProperty("imdbRating")
	public String getImdbRating() {
		return imdbRating;
	}

	@JsonProperty("imdbRating")
	public void setImdbRating(String imdbRating) {
		this.imdbRating = imdbRating;
	}

	@JsonProperty("imdbVotes")
	public String getImdbVotes() {
		return imdbVotes;
	}

	@JsonProperty("imdbVotes")
	public void setImdbVotes(String imdbVotes) {
		this.imdbVotes = imdbVotes;
	}

	@JsonProperty("imdbID")
	public String getImdbID() {
		return imdbID;
	}

	@JsonProperty("imdbID")
	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}

	@JsonProperty("Type")
	public String getType() {
		return type;
	}

	@JsonProperty("Type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("DVD")
	public String getDVD() {
		return dVD;
	}

	@JsonProperty("DVD")
	public void setDVD(String dVD) {
		this.dVD = dVD;
	}

	@JsonProperty("BoxOffice")
	public String getBoxOffice() {
		return boxOffice;
	}

	@JsonProperty("BoxOffice")
	public void setBoxOffice(String boxOffice) {
		this.boxOffice = boxOffice;
	}

	@JsonProperty("Production")
	public String getProduction() {
		return production;
	}

	@JsonProperty("Production")
	public void setProduction(String production) {
		this.production = production;
	}

	@JsonProperty("Website")
	public String getWebsite() {
		return website;
	}

	@JsonProperty("Website")
	public void setWebsite(String website) {
		this.website = website;
	}

	@JsonProperty("Response")
	public String getResponse() {
		return response;
	}

	@JsonProperty("Response")
	public void setResponse(String response) {
		this.response = response;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new org.apache.commons.lang3.builder.ToStringBuilder(this).append("title", title).append("year", year)
				.append("rated", rated).append("released", released).append("runtime", runtime).append("genre", genre)
				.append("director", director).append("writer", writer).append("actors", actors).append("plot", plot)
				.append("language", language).append("country", country).append("awards", awards)
				.append("poster", poster).append("ratings", ratings).append("metascore", metascore)
				.append("imdbRating", imdbRating).append("imdbVotes", imdbVotes).append("imdbID", imdbID)
				.append("type", type).append("dVD", dVD).append("boxOffice", boxOffice).append("production", production)
				.append("website", website).append("response", response)
				.append("additionalProperties", additionalProperties).toString();
	}

}