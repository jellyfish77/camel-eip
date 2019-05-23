package movies;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import activemq.ActiveMQClient;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitTests extends CamelSpringTestSupport {

	String csvFile = "color,director_name,num_critic_for_reviews,duration,director_facebook_likes,actor_3_facebook_likes,actor_2_name,actor_1_facebook_likes,gross,genres,actor_1_name,movie_title,num_voted_users,cast_total_facebook_likes,actor_3_name,facenumber_in_poster,plot_keywords,movie_imdb_link,num_user_for_reviews,language,country,content_rating,budget,title_year,actor_2_facebook_likes,imdb_score,aspect_ratio,movie_facebook_likes\n"
			+ "\n"
			+ "Color,James Cameron,723,178,0,855,Joel David Moore,1000,760505847,Action|Adventure|Fantasy|Sci-Fi,CCH Pounder,Avatar ,886204,4834,Wes Studi,0,avatar|future|marine|native|paraplegic,http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1,3054,English,USA,PG-13,237000000,2009,936,7.9,1.78,33000";

	String csvFileLine = "Color,James Cameron,723,178,0,855,Joel David Moore,1000,760505847,Action|Adventure|Fantasy|Sci-Fi,CCH Pounder,Avatar ,886204,4834,Wes Studi,0,avatar|future|marine|native|paraplegic,http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1,3054,English,USA,PG-13,237000000,2009,936,7.9,1.78,33000";
	
	String xmlFileRow = "<Movie><Title>Avatar</Title><Gross>760505847</Gross><Genres><Genre>Action</Genre><Genre>Adventure</Genre><Genre>Fantasy</Genre><Genre>Sci-Fi</Genre></Genres><Color>Color</Color><UserVotes>886204</UserVotes><NumUsersReviewed>3054</NumUsersReviewed><Language>English</Language><Rating>PG-13</Rating><Budget>237000000</Budget><Year>2009</Year><ImdbScore>7.9</ImdbScore><ImdbLink>http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1</ImdbLink><AspectRatio>1.78</AspectRatio><Country>USA</Country><FacebookLikes>33000</FacebookLikes><NumPosterFaces>0</NumPosterFaces><PlotKeywords><PlotKeyword>avatar</PlotKeyword><PlotKeyword>future</PlotKeyword><PlotKeyword>marine</PlotKeyword><PlotKeyword>native</PlotKeyword><PlotKeyword>paraplegic</PlotKeyword></PlotKeywords><Director><Name>James Cameron</Name><FacebookLikes>0</FacebookLikes></Director><NumReviews>723</NumReviews><Duration>178</Duration><Cast><Actors><Actor><Biling>1</Biling><Name>CCH Pounder</Name><FacebookLikes>1000</FacebookLikes></Actor><Actor><Biling>2</Biling><Name>Joel David Moore</Name><FacebookLikes>936</FacebookLikes></Actor><Actor><Biling>3</Biling><Name>Wes Studi</Name><FacebookLikes>855</FacebookLikes></Actor></Actors></Cast></Movie>";
	
	String enrichedXmlRow = "<Movie><Title>Avatar</Title><ImdbId>tt0499549</ImdbId><Gross>760505847</Gross><Format><Type>movie</Type><Color>Color</Color><Language>English</Language><AspectRatio>1.78</AspectRatio><Runtime>162</Runtime></Format><Production><Countries><Country>UK</Country><Country>USA</Country></Countries><Budget>237000000</Budget><Year>2009</Year><ReleaseDate>2009-12-18Z</ReleaseDate><DvdDate>2010-04-22Z</DvdDate><Website>http://www.avatarmovie.com/</Website></Production><Genres><Genre>Action</Genre><Genre>Adventure</Genre><Genre>Fantasy</Genre><Genre>Sci-Fi</Genre></Genres><Plot><Description>A paraplegic marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.</Description><PlotKeywords><PlotKeyword>avatar</PlotKeyword><PlotKeyword>future</PlotKeyword><PlotKeyword>marine</PlotKeyword><PlotKeyword>native</PlotKeyword><PlotKeyword>paraplegic</PlotKeyword></PlotKeywords></Plot><Poster>https://m.media-amazon.com/images/M/MV5BMTYwOTEwNjAzMl5BMl5BanBnXkFtZTcwODc5MTUwMw@@._V1_SX300.jpg</Poster><Awards>Won 3 Oscars. Another 85 wins &amp; 128 nominations.</Awards><UserVotes>886204</UserVotes><NumUsersReviewed>3054</NumUsersReviewed><Rating>PG-13</Rating><ImdbLink>http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1</ImdbLink><Country>USA</Country><FacebookLikes>33000</FacebookLikes><NumPosterFaces>0</NumPosterFaces><Director><Name>James Cameron</Name><FacebookLikes>0</FacebookLikes></Director><Writers><Writer>James Cameron</Writer></Writers><NumReviews>723</NumReviews><Cast><Actors><Actor><Biling>1</Biling><Name>CCH Pounder</Name><FacebookLikes>1000</FacebookLikes></Actor><Actor><Biling>2</Biling><Name>Joel David Moore</Name><FacebookLikes>936</FacebookLikes></Actor><Actor><Biling>3</Biling><Name>Wes Studi</Name><FacebookLikes>855</FacebookLikes></Actor><Actor><Name>Sam Worthington</Name></Actor><Actor><Name>Zoe Saldana</Name></Actor><Actor><Name>Sigourney Weaver</Name></Actor><Actor><Name>Stephen Lang</Name></Actor></Actors></Cast><Ratings><Rating><Source>Internet Movie Database</Source><Value>7.8/10</Value></Rating><Rating><Source>Rotten Tomatoes</Source><Value>82%</Value></Rating><Rating><Source>Metacritic</Source><Value>83/100</Value></Rating></Ratings></Movie>";
	
	Logger LOG = LoggerFactory.getLogger(UnitTests.class);

	// Load the Camel context file into a Spring application context
	@Override
	protected AbstractXmlApplicationContext createApplicationContext() {
		// Load the routes from the classpath
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}

	/*
	 * @Before public void removeQueues() throws Exception {
	 * LOG.info("Removing Queues..."); ActiveMQClient amqc = new
	 * ActiveMQClient("localhost", "user", "user", "localhost");
	 * amqc.modifyQueue("movieCsvFile", "removeQueue");
	 * amqc.modifyQueue("ActiveMQ.DLQ", "removeQueue");
	 * amqc.modifyQueue("validatedMovieCmmRecord", "removeQueue");
	 * LOG.info("Finished Removing Queues"); }
	 */

	/*
	 * Test conversion of csv file to csv line
	 */
	@Test
	public void testSplitCsvFileToLine() throws Exception {
		RouteBuilder rb = new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("direct:movieCsvLine").to("log:intercepted?showAll=true")
						.skipSendToOriginalEndpoint().to("mock:movieCsvLine");
			}
		};

		// add intercepter to route
		context.getRouteDefinition("splitCsvFileRoute").adviceWith(context, rb);

		MockEndpoint movieCsvLine = getMockEndpoint("mock:movieCsvLine");
		movieCsvLine.expectedMessageCount(1);
		movieCsvLine.expectedBodiesReceived(csvFileLine);

		template.sendBodyAndHeader("direct:movieCsvFile", csvFile, Exchange.FILE_NAME,
				"testSplitCsvWithMock.csv");

		assertMockEndpointsSatisfied();
		// movieCsvLine.assertIsSatisfied();
	}

	/*
	 * Test conversion of csv file to xml row
	 */
	@Test
	public void testConvertCsvLineToXml() throws Exception {
		RouteBuilder rb = new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("direct:movieXmlRow").to("log:intercepted?showAll=true")
						.skipSendToOriginalEndpoint().to("mock:movieXmlRow");
			}
		};

		// add intercepter to route
		context.getRouteDefinition("convertCsvToXmlRoute").adviceWith(context, rb);

		MockEndpoint movieXmlRow = getMockEndpoint("mock:movieXmlRow");
		movieXmlRow.expectedMessageCount(1);
		movieXmlRow.expectedBodiesReceived(xmlFileRow);

		template.sendBodyAndHeader("direct:movieCsvLine", csvFileLine, Exchange.FILE_NAME,
				"testSplitCsvWithMock.csv");

		assertMockEndpointsSatisfied();
	}

	/*
	 * Test xml validation route
	 */
	@Test
	public void testXmlValidationPos() throws Exception {
						
		RouteBuilder rb = new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("activemq:queue:validatedMovieCmmRecord").to("log:intercepted?showAll=true")
						.skipSendToOriginalEndpoint().to("mock:validatedMovieCmmRecord");
			}
		};

		// add intercepter to route
		context.getRouteDefinition("validateXmlRoute").adviceWith(context, rb);

		MockEndpoint movieXmlRow = getMockEndpoint("mock:validatedMovieCmmRecord");
		movieXmlRow.expectedMessageCount(1);
		
		template.sendBodyAndHeader("direct:enrichedMovieXmlRecord", enrichedXmlRow, Exchange.FILE_NAME,
				"testXmlValidationPos.xml");

		assertMockEndpointsSatisfied();
	}

}
