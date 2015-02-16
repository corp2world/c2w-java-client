package com.c2w.client.core.service.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.c2w.client.core.message.Message;
import com.c2w.client.core.message.MessageResponse;
import com.c2w.client.core.service.Result;
import com.c2w.client.core.service.ServiceException;

/**
 * Unit test case for {@link com.c2w.client.core.service.http.HttpService}
 * 
 * @author ptrvif
 *
 */
@RunWith(JMockit.class)
public class HttpServiceTest {

	/*
	 * Test API token provided through 'set' method
	 */
	private String apiToken = "API|11112222...";
	
	/*
	 * Test API key provided through 'set' method
	 */
	private String apiKey = "111122223333...";
	
	/*
	 * Test API token provided through system property
	 */
	private String envApiToken = "ENV - API|11112222...";
	
	/*
	 * Test API key provided through system property 
	 */
	private String envApiKey = "ENV - 111122223333...";

	
	@Before
	public void setUp() {
		
		Logger.getLogger(HttpService.class.getName()).setLevel(Level.OFF);
		
	}
	
	@Test
	public void testHttpService() {
		
		/*
		 * Just ensure no exception
		 */
		new HttpService();
		
	}

	@Test
	public void testSend( @Mocked final DefaultHttpClient httpClient, @Mocked final HttpParams params, 
			@Mocked final HttpResponse response, @Mocked final StatusLine status, @Mocked final HttpEntity entity, @Mocked final EntityUtils utils) throws Exception {
		
		final String responseAsString = "{\"status\": \"OK\", \"response\": \"Message accepted\", \"properties\": { \"messageId\": \"1000\" } }";
		
		new NonStrictExpectations() {{
			
			httpClient.getParams(); result= params;
			httpClient.execute(withInstanceOf(HttpPost.class)); result = response;
			response.getStatusLine(); result = status;
			status.getStatusCode(); result = HttpStatus.SC_OK;
			response.getEntity(); result = entity;
			EntityUtils.toString(entity); result = responseAsString;
		}};
		
		HttpService service = new HttpService();
		service.setApiKey(apiKey);
		service.setApiToken(apiToken);
		Message message = new Message("Topic", "Text");
		Result result = service.send(message);
		assertNotNull(result);
		assertEquals(result.getStatus(), Result.Status.OK);
		
		new Verifications() {{
			httpClient.execute(withInstanceOf(HttpPost.class)); times=1;
		}};
		
	}

	
	/**
	 * Test case for 'waitForResponse' method
	 */
	@Test
	public void testWaitForResponse( @Mocked final DefaultHttpClient httpClient, @Mocked final HttpParams params, 
			@Mocked final HttpResponse response, @Mocked final StatusLine status, @Mocked final HttpEntity entity, @Mocked final EntityUtils utils) throws Exception {
		
		final String responseAsString = "[ { \"properties\": {},  \"messageId\": 100, \"timestamp\": 1414290538079,  \"respondedOption\": \"Confirm\"," +
		" \"userId\": \"recipient1@mail.com\", \"channelId\": 5} ]";
		
		new NonStrictExpectations() {{
			
			httpClient.getParams(); result= params;
			httpClient.execute(withInstanceOf(HttpGet.class)); result = response;
			response.getStatusLine(); result = status;
			status.getStatusCode(); result = HttpStatus.SC_OK;
			response.getEntity(); result = entity;
			EntityUtils.toString(entity); result = responseAsString;
		}};
		
		HttpService service = new HttpService();
		service.setApiKey(apiKey);
		service.setApiToken(apiToken);
		Message message = new Message("Topic", "Text");
		List<MessageResponse> responseList = service.waitForResponse(1l, 60);
		
		assertEquals(responseList.get(0).getRespondedOption() , "Confirm" );
		
		new Verifications() {{
			httpClient.execute(withInstanceOf(HttpGet.class)); times=1;
		}};
	}
	
	
	/**
	 * Test 'start' method when no credentials are provided
	 * The expected behavior is thrown Exception
	 */
	@Test
	public void testStartNoCredentials() {
		
		// Service should throw exception if token is not set
		HttpService service = new HttpService();
		Exception exception = null;
		try {
			service.start();
		} catch (ServiceException e) {
			exception = e;
		}
		assertNotNull(exception);
		
		// Service should throw exception if API key is not set
		service = new HttpService();
		exception = null;
		service.setApiToken(apiToken);
		try {
			service.start();
		} catch (ServiceException e) {
			exception = e;
		}
		assertNotNull(exception);
		
	}	
	
	/**
	 * Test 'start' method when credentials are provided as 'system properties'.
	 * The expected behavior is to use provided credentials
	 * @param credentials  mocked object holding user credentials
	 */
	@Test
	public void testStartWithSystemPropertiesCredentials(@Mocked UsernamePasswordCredentials credentials) throws ServiceException {
		
		new NonStrictExpectations() {{
			 
		}};
		
		System.setProperty(HttpService.API_TOKEN, envApiToken);
		System.setProperty(HttpService.API_KEY, envApiKey);
		HttpService service = new HttpService();
		service.start();
		service.stop();
		// VErify that environment properties were used for credentials
		new Verifications() {{
			new UsernamePasswordCredentials(envApiToken, envApiKey); times=1;
		}};
	}	
	
	
	/**
	 * Test 'start' method when credentials are provided via 'set' method.
	 * The expected behavior is to use provided credentials
	 * @param credentials  mocked object holding user credentials
	 */
	@Test
	public void testStartWithSetCredentials(@Mocked UsernamePasswordCredentials credentials) throws ServiceException {
		
		new NonStrictExpectations() {{
			 
		}};
		
		// Ensure system property is not set
		System.getProperties().remove(HttpService.API_TOKEN);
		System.getProperties().remove(HttpService.API_KEY);
		
		HttpService service = new HttpService();
		service.setApiToken(apiToken);
		service.setApiKey(apiKey);
		service.start();
		service.stop();
		// VErify that environment properties were used for credentials
		new Verifications() {{
			new UsernamePasswordCredentials(apiToken, apiKey); times=1;
		}};
	}
	
	
	/**
	 * Test 'start' method when credentials are provided via 'set' method as well as System properties.
	 * The expected behavior is to use credentials provided via 'set' method
	 * @param credentials  mocked object holding user credentials
	 */
	@Test
	public void testStartWithSetAndSystemPropertiesCredentials(@Mocked UsernamePasswordCredentials credentials) throws ServiceException {
		
		new NonStrictExpectations() {{
			 
		}};
		
		// Set system property credentials
		System.setProperty(HttpService.API_TOKEN, envApiToken);
		System.setProperty(HttpService.API_KEY, envApiKey);
		
		HttpService service = new HttpService();
		service.setApiToken(apiToken);
		service.setApiKey(apiKey);
		service.start();
		service.stop();
		// VErify that environment properties were used for credentials
		new Verifications() {{
			new UsernamePasswordCredentials(apiToken, apiKey); times=1;
		}};
	}
	
	
	@Test
	public void testStop(@Mocked final DefaultHttpClient httpClient, @Mocked final ClientConnectionManager connectionManager,
			@Mocked final HttpParams params) throws ServiceException {
		
		new NonStrictExpectations() {{
			httpClient.getParams(); result = params;
			httpClient.getConnectionManager(); result = connectionManager;
		}};

		HttpService service = new HttpService();
		service.setApiToken(apiToken);
		service.setApiKey(apiKey);
		service.start();
		service.stop();
		
		new Verifications() {{
			connectionManager.shutdown(); times=1;
		}};
	}
}
