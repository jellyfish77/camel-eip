package activemq;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.MalformedObjectNameException;

public class ActiveMQClient {

	private J4pClient j4pClient;
	private String brokerName;
	private String host;
	{

	}

	public static void main(String[] args) throws MalformedObjectNameException, J4pException {
		ActiveMQClient activeMQClient = new ActiveMQClient("localhost", "user", "user", "localhost");

		try {
			activeMQClient.modifyQueue("movieCsvFile", "removeQueue");
			activeMQClient.modifyQueue("movieCsvFile", "addQueue");
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(activeMQClient.getNumberOfConsumedMessages("ActiveMQ.DLQ"));
		System.out.println(activeMQClient.getNumberOfEnqueuedMessages("ActiveMQ.DLQ"));

		System.out.println(activeMQClient.getNumberOfConsumedMessages("movieCsvFile"));
		System.out.println(activeMQClient.getNumberOfEnqueuedMessages("movieCsvFile"));

	}

	public ActiveMQClient(String host, String user, String password, String brokerName) {
		this.brokerName = brokerName;
		this.host = host;
		j4pClient = J4pClient.url("http://" + host + ":8161/api/jolokia").user(user).password(password).build();
	}

	public Long getNumberOfConsumedMessages(String queueName) throws MalformedObjectNameException, J4pException {
		J4pReadRequest j4pReadRequest = new J4pReadRequest("org.apache.activemq:brokerName=" + brokerName
				+ ",destinationName=" + queueName + ",destinationType=Queue,type=Broker", "DequeueCount");
		J4pResponse<J4pReadRequest> response = j4pClient.execute(j4pReadRequest);
		return response.getValue();
	}

	public Long getNumberOfEnqueuedMessages(String queueName) throws MalformedObjectNameException, J4pException {
		J4pReadRequest j4pReadRequest = new J4pReadRequest("org.apache.activemq:brokerName=" + brokerName
				+ ",destinationName=" + queueName + ",destinationType=Queue,type=Broker", "EnqueueCount");
		J4pResponse<J4pReadRequest> response = j4pClient.execute(j4pReadRequest);
		return response.getValue();
	}

	public String modifyQueue(String queueName, String operation)
			throws ClientProtocolException, IOException, URISyntaxException {

		String username = "admin";
		String password = "admin";
		URI mqUrl = new URI(host + 8161);
		// HttpHost targetHost = new HttpHost(mqUrl.getHost(), mqUrl.getPort(), "http");
		HttpHost targetHost = new HttpHost(host, 8161, "http");

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache();
		authCache.put(targetHost, new BasicScheme());

		// Add AuthCache to the execution context
		final HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);

		HttpClient client = HttpClientBuilder.create().build();

		// String uri = "http://" + mqUrl.getHost() + ":" + mqUrl.getPort() +
		// "/hawtio/jolokia/exec/org.apache.activemq:type=Broker,brokerName=localhost/removeQueue/"
		// + queueName;
		String uri = "http://" + host + ":" + 8161 + "/api/jolokia/exec/org.apache.activemq:type=Broker,brokerName="
				+ brokerName + "/" + operation + "/" + queueName;

		System.out.println(uri);

		HttpResponse response = client.execute(new HttpGet(uri), context);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException(response.getStatusLine().toString());
		}
		return response.getEntity().getContent().toString();
	}

}
