package movies;

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

public class SplitCsvRouteMockTests extends CamelSpringTestSupport {

	String csvFile = "color,director_name,num_critic_for_reviews,duration,director_facebook_likes,actor_3_facebook_likes,actor_2_name,actor_1_facebook_likes,gross,genres,actor_1_name,movie_title,num_voted_users,cast_total_facebook_likes,actor_3_name,facenumber_in_poster,plot_keywords,movie_imdb_link,num_user_for_reviews,language,country,content_rating,budget,title_year,actor_2_facebook_likes,imdb_score,aspect_ratio,movie_facebook_likes\n"
			+ "\n"
			+ "Color,James Cameron,723,178,0,855,Joel David Moore,1000,760505847,Action|Adventure|Fantasy|Sci-Fi,CCH Pounder,Avatar ,886204,4834,Wes Studi,0,avatar|future|marine|native|paraplegic,http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1,3054,English,USA,PG-13,237000000,2009,936,7.9,1.78,33000";

	Logger LOG = LoggerFactory.getLogger(SplitCsvRouteMockTests.class);
	
	// Load the Camel context file into a Spring application context
	@Override
	protected AbstractXmlApplicationContext createApplicationContext() {
		// Load the routes from the classpath
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}

	@Before
	public void removeQueues() throws Exception {
		LOG.info("Removing Queues...");
		ActiveMQClient amqc = new ActiveMQClient("localhost", "user", "user", "localhost");
		amqc.modifyQueue("movieCsvFile", "removeQueue");
		amqc.modifyQueue("ActiveMQ.DLQ", "removeQueue");		
		amqc.modifyQueue("validatedMovieCmmRecord", "removeQueue");
		LOG.info("Finished Removing Queues");
	}
	
	@Test
	public void testSplitCsvWithMock() throws Exception {
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
		
		template.sendBody("activemq:queue:movieCsvFile", csvFile);
				
		movieCsvLine.assertIsSatisfied();		
	}
	
	@Test
	public void testSplitCsvWithMockAndSkip() throws Exception {
		AdviceWithRouteBuilder rb = new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {				
				mockEndpointsAndSkip("direct:movieCsvLine");						
			}
		};

		// add intercepter to route
		context.getRouteDefinition("splitCsvFileRoute").adviceWith(context, rb);
				
		//getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
	    getMockEndpoint("direct:movieCsvLine").expectedMessageCount(1);	    
				
		template.sendBody("activemq:queue:movieCsvFile", csvFile);
				
		assertMockEndpointsSatisfied();		
	}
	

}
