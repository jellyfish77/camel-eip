package movies;

import java.net.ConnectException;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.context.annotation.*;

import activemq.ActiveMQClient;

/*
 * Unit test for Spring XML based routes
 */
public class CsvFileToCmmTxTest extends CamelSpringTestSupport {

	String csvFile = "color,director_name,num_critic_for_reviews,duration,director_facebook_likes,actor_3_facebook_likes,actor_2_name,actor_1_facebook_likes,gross,genres,actor_1_name,movie_title,num_voted_users,cast_total_facebook_likes,actor_3_name,facenumber_in_poster,plot_keywords,movie_imdb_link,num_user_for_reviews,language,country,content_rating,budget,title_year,actor_2_facebook_likes,imdb_score,aspect_ratio,movie_facebook_likes\n"
			+ "\n"
			+ "Color,James Cameron,723,178,0,855,Joel David Moore,1000,760505847,Action|Adventure|Fantasy|Sci-Fi,CCH Pounder,Avatar ,886204,4834,Wes Studi,0,avatar|future|marine|native|paraplegic,http://www.imdb.com/title/tt0499549/?ref_=fn_tt_tt_1,3054,English,USA,PG-13,237000000,2009,936,7.9,1.78,33000";

	// Load the Camel context file into a Spring application context
	@Override
	protected AbstractXmlApplicationContext createApplicationContext() {
		//Load the routes from the classpath 
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}

	@Test
	public void testTransactedQueue() throws Exception {

		ActiveMQClient amqc = new ActiveMQClient("localhost", "user", "user", "localhost");
		
		amqc.modifyQueue("movieCsvFile", "removeQueue");
		amqc.modifyQueue("ActiveMQ.DLQ", "removeQueue");
		Thread.sleep(1000);
				
		
		// cause exception on writing to queue
		// camel will re-route original message from activemq:queue:movieCsvFile to ActiveMQ.DLQ
		RouteBuilder rb = new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint("activemq:queue:validatedMovieCmmRecord").skipSendToOriginalEndpoint()
						.throwException(new ConnectException(
								"Simulated failure sending to activemq:queue:validatedMovieCmmRecord"));
			}
		};

		// adviseWith enhances our route by adding the interceptor from the route builder
		// this allows us here directly in the unit test to add interceptors so we can
		// simulate the connection failure
		context.getRouteDefinition("validateXmlRoute").adviceWith(context, rb);

		template.sendBodyAndHeader("activemq:queue:movieCsvFile", csvFile, "CamelFileName", "testCsvString");
		
		// wait for the route to complete all delivery attempts
		Thread.sleep(180000);				
				
		assertTrue(amqc.getNumberOfEnqueuedMessages("movieCsvFile") == 1);
		assertTrue(amqc.getNumberOfConsumedMessages("movieCsvFile") == 1);
		
		assertTrue(amqc.getNumberOfEnqueuedMessages("ActiveMQ.DLQ") == 1);
		assertTrue(amqc.getNumberOfConsumedMessages("ActiveMQ.DLQ") == 0);
	}

}
