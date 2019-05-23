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
					.skipSendToOriginalEndpoint()
					.to("mock:movieCsvLine");						
			}
		};

		// add intercepter to route
		context.getRouteDefinition("splitCsvFileRoute").adviceWith(context, rb);
				
		MockEndpoint movieCsvLine = getMockEndpoint("mock:movieCsvLine");
		movieCsvLine.expectedMessageCount(1);
		movieCsvLine.expectedBodiesReceived("Color,James Cameron,723,178,0,855,Joel David Moore,1000,760505847,Action|Adventure|Fantasy|Sci-Fi,CCH Pounder,Avatar ,886204,4834,Wes Studi,0,avatar|future|marine|native|paraplegic,http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1,3054,English,USA,PG-13,237000000,2009,936,7.9,1.78,33000");		
		
		template.sendBodyAndHeader("activemq:queue:movieCsvFile", csvFile, Exchange.FILE_NAME, "testSplitCsvWithMock.csv");
		
		assertMockEndpointsSatisfied();		
		//movieCsvLine.assertIsSatisfied();		
	}	
	
	/*
	 * Test conversion of csv file to xml row
	 */
	@Test
	public void testConvertCsvFileToXml() throws Exception {
		RouteBuilder rb = new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("direct:movieXmlRow").to("log:intercepted?showAll=true")
					.skipSendToOriginalEndpoint()
					.to("mock:movieXmlRow");						
			}
		};

		// add intercepter to route
		context.getRouteDefinition("convertCsvToXmlRoute").adviceWith(context, rb);
						 
		MockEndpoint movieXmlRow = getMockEndpoint("mock:movieXmlRow");
		movieXmlRow.expectedMessageCount(1);
		movieXmlRow.expectedBodiesReceived("<Movie><Title>Avatar</Title><Gross>760505847</Gross><Genres><Genre>Action</Genre><Genre>Adventure</Genre><Genre>Fantasy</Genre><Genre>Sci-Fi</Genre></Genres><Color>Color</Color><UserVotes>886204</UserVotes><NumUsersReviewed>3054</NumUsersReviewed><Language>English</Language><Rating>PG-13</Rating><Budget>237000000</Budget><Year>2009</Year><ImdbScore>7.9</ImdbScore><ImdbLink>http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1</ImdbLink><AspectRatio>1.78</AspectRatio><Country>USA</Country><FacebookLikes>33000</FacebookLikes><NumPosterFaces>0</NumPosterFaces><PlotKeywords><PlotKeyword>avatar</PlotKeyword><PlotKeyword>future</PlotKeyword><PlotKeyword>marine</PlotKeyword><PlotKeyword>native</PlotKeyword><PlotKeyword>paraplegic</PlotKeyword></PlotKeywords><Director><Name>James Cameron</Name><FacebookLikes>0</FacebookLikes></Director><NumReviews>723</NumReviews><Duration>178</Duration><Cast><Actors><Actor><Biling>1</Biling><Name>CCH Pounder</Name><FacebookLikes>1000</FacebookLikes></Actor><Actor><Biling>2</Biling><Name>Joel David Moore</Name><FacebookLikes>936</FacebookLikes></Actor><Actor><Biling>3</Biling><Name>Wes Studi</Name><FacebookLikes>855</FacebookLikes></Actor></Actors></Cast></Movie>");
						
		template.sendBodyAndHeader("activemq:queue:movieCsvFile", csvFile, Exchange.FILE_NAME, "testSplitCsvWithMock.csv");
		
		assertMockEndpointsSatisfied();
	}	
	
}
