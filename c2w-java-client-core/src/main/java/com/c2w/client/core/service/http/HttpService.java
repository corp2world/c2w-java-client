package com.c2w.client.core.service.http;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.c2w.client.core.message.Message;
import com.c2w.client.core.message.MessageResponse;
import com.c2w.client.core.service.Result;
import com.c2w.client.core.service.Service;
import com.c2w.client.core.service.ServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HTTP-based Corp2World message service implementation
 * This implementation requires certain system properties to be set correctly. They are:
 * <ul>
 *  <li>com.c2w.service.url - URL of the Corp2World message service </li>
 *  <li>com.c2w.client.token - client name , used for the authentication </li>
 *  <li>com.c2w.client.key - client password , used for the authentication </li>
 *  <li>com.c2w.truststore.file - key-store file with trusted Corp2World server certificate. In most cases should not be required.</li> 
 *  <li>com.c2w.truststore.password - key-store password for trusted Corp2World server certificate. In most cases should not be required</li>
 *  
 *  If you need to use a proxy for internet access, use the following optional properties:
 *  <li>com.c2w.service.proxy.host - proxy host</li>
 *  <li>com.c2w.service.proxy.port - proxy port</li>
 *  <li>com.c2w.service.proxy.user - proxy user name (if proxy requires authorization)</li>
 *  <li>com.c2w.service.proxy.password - proxy password (if proxy requires authorization)</li>
 * </ul>
 * 
 *  This properties can be set through Java JVM parameters passed as -D<parameter_name>=<parameter_value>  or
 *  from Java code as <br>
 *  
 *  System.getProperties().setProperty("<property_name>", "<property_value>");
 *  
 */
public class HttpService implements Service {
	
	/**
	 * Default service URL
	 */
	public static final String DEFAULT_URL = "https://www.corp2world.com:9443/rest";
	
	/**
	 * System property to specify Corp2World server URL 
	 */
	public static final String SERVER_URL = "com.c2w.service.url";
	
	/**
	 * System property to specify Corp2World client name
	 */
	public static final String CLIENT_NAME = "com.c2w.client.token";
	
	/**
	 * System property to specify Corp2World client password
	 */
	public static final String CLIENT_PASSWORD = "com.c2w.client.key";
	
	/**
	 * System property to specify Corp2World certificate file storage
	 */
	public static final String CERTIFICATE_STORAGE_FILE = "com.c2w.truststore.file";
	
	/**
	 * Default value for certificate storage file to use
	 */
	public static final String DEFAULT_CERTIFICATE_STORAGE_FILE = "corp2world-trusted.jks";
	
	/**
	 * System property to specify Corp2World certificate file storage
	 */
	public static final String CERTIFICATE_STORAGE_PASSWORD = "com.c2w.truststore.password";
	
	/**
	 * Default value for certificate storage file to use
	 */
	public static final String DEFAULT_CERTIFICATE_STORAGE_PASSWORD = "password";
	
	/**
	 * Proxy Host
	 */
	public static final String PROXY_HOST = "com.c2w.service.proxy.host";
	
	/**
	 * Proxy Port
	 */
	public static final String PROXY_PORT = "com.c2w.service.proxy.port";
	
	/**
	 * Proxy User
	 */
	public static final String PROXY_USER = "com.c2w.service.proxy.user";
	
	/**
	 * Proxy Password
	 */
	public static final String PROXY_PASSWORD = "com.c2w.service.proxy.password";
	
	/*
	 * Log4J logger
	 */
	private static Log log = LogFactory.getLog(HttpService.class);
	
	/*
	 * Path to 'post message' RESTful resource
	 */
	private static final String PATH_POST_MESSAGE = "/message/post";
	
	/*
	 * Path to 'get message response' RESTful resource
	 */
	private static final String PATH_GET_RESPONSE = "/message/response";
	
	/*
	 * JSON object mapper
	 */
	private ObjectMapper mapper;
	
	/*
	 * Connection state
	 */
	private boolean connected = false;
	
	/*
	 * HTTP client
	 */
	private DefaultHttpClient httpClient;
	
	/*
	 * Service URL
	 */
	private URL url;
	
	/*
	 * Service URL string
	 */
	private String urlAsString = DEFAULT_URL;
	
	/**
	 * Create new service instance
	 */
	public HttpService() {
	}
	
	/**
	 * Send message
	 */
	@Override
	public Result send(Message message) throws ServiceException {

		Result result;
		
		try {
			
			// Start service
			if(!connected)
				start();
			
			String messageAsJson = mapper.writeValueAsString(message);
			
			// Log message
			log.debug("Sending message: " + messageAsJson);
			
			// Prepare and execute POST request
			HttpPost httpPost = new HttpPost(new URL(urlAsString + PATH_POST_MESSAGE).toExternalForm());
			StringEntity entity = new StringEntity(messageAsJson, StandardCharsets.UTF_8);		
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			
			log.debug("Response: " + response.getStatusLine().toString());
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String responseAsString = EntityUtils.toString(response.getEntity());
				log.debug("Response content: " +   responseAsString);
				result = mapper.readValue(responseAsString, Result.class);
			}
			else {
				result = new Result(Result.Status.ERROR, response.getStatusLine().toString());
			}
			
		}catch(Exception e) {
			log.error("Cannot send message because of " + e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		// Return result
		return result;
	}

	
	@Override
	public List<MessageResponse> waitForResponse(long messageId, long timeoutSec) throws ServiceException {
		
		List<MessageResponse> messageResponseList = null;
		
		try {
			
			// Start service
			if(!connected)
				start();
			
			// Try to get response in the loop
			long time = System.currentTimeMillis();

			while((messageResponseList == null || messageResponseList.size()==0) && 
					(time+timeoutSec*1000 > System.currentTimeMillis()) ) {
				
				// Log message
				log.debug("Getting message response for messageID: " + messageId);

				// Prepare and execute POST request
				HttpGet httpGet = new HttpGet(new URL(urlAsString + PATH_GET_RESPONSE + "?messageId="+messageId).toExternalForm());
				httpGet.getParams().setLongParameter("messageId", messageId);
				HttpResponse response = httpClient.execute(httpGet);

				log.debug("Response: " + response.getStatusLine().toString());

				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String responseAsString = EntityUtils.toString(response.getEntity());
					log.debug("Response content: " +   responseAsString);
					messageResponseList = mapper.readValue(responseAsString, new TypeReference<List<MessageResponse>>(){});
				}
				
				// Sleep
				if(messageResponseList == null || messageResponseList.size()==0)
					try {
						Thread.sleep(1000);
					}catch(InterruptedException e) {}
			}
			
		}catch(Exception e) {
			log.error("Cannot send message because of " + e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		// Return response
		return messageResponseList;
	}	
	
	@Override
	public void start() throws ServiceException {
		log.info("Starting service...");
		try {
			
			// Get Server URL
			urlAsString = System.getProperty(SERVER_URL, urlAsString);
			
			log.debug("Connecting to the service at " + urlAsString);
			if(urlAsString == null || urlAsString.length() < 1) {
				log.error("Server URL must be specified for HttpTransport, please refer documentation for details");
				throw new ServiceException("Cannot initialize HttpTransport, server URL is not specified");
			}	
			url = new URL(urlAsString);
			
			// Get client name
			String clientName = System.getProperty(CLIENT_NAME);
			if(clientName == null || clientName.length() < 1) {
				log.error("Client name must be specified, please refer documentation for details");
				throw new ServiceException("Cannot initialize HttpTransport, client name is not specified");
			}	

			// Get client password
			String clientPassword = System.getProperty(CLIENT_PASSWORD);
			if(clientName == null || clientName.length() < 1) {
				log.error("Client password must be specified, please refer documentation for details");
				throw new ServiceException("Cannot initialize HttpTransport, client password is not specified");
			}	
			
			// Initialize client
			SchemeRegistry sr = new SchemeRegistry();
			
			// If HTTPS
			if(url.getProtocol().toLowerCase().startsWith("https")) {
				
				String certificateFile = System.getProperty(CERTIFICATE_STORAGE_FILE);
				
				String certificatePassword = System.getProperty(CERTIFICATE_STORAGE_PASSWORD);
				
				// Initialize SSL layer
				SSLContext ctx=null;

				KeyStore trustStore = null;

				TrustManagerFactory tmf = null;
				
				try {				

					// If certificate trusted storage is specified
					if(certificateFile!=null && certificateFile.length()>0 && certificatePassword!=null && certificatePassword.length()>0) {

						log.debug("Loading trusted certificates from " + certificateFile);

						URL fileUrl = ClassLoader.getSystemClassLoader().getResource(certificateFile);

						// If store exists
						if(fileUrl != null) {
							trustStore = KeyStore.getInstance("JKS");
							trustStore.load(getClass().getResourceAsStream( certificateFile ), certificatePassword.toCharArray());
							tmf=TrustManagerFactory.getInstance("SunX509");
							tmf.init(trustStore);
						}
						else {
							log.warn("Could not load file " + certificateFile + " , ignoring.");
						}
					}

					ctx = SSLContext.getInstance("SSL");
					ctx.init(null, null, null);

				} catch (Exception e) {
					log.error("Cannot initialize secure HTTPS communication channel, cannot load server certificate from the specified store: " + certificateFile, e);
				}
				
				SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				Scheme https = new Scheme( url.getProtocol(), ( url.getPort() > 0 ? url.getPort() : url.getDefaultPort() ), ssf);		
				sr.register(https);
			} 
			
			// Always register HTTP schema with default port
			Scheme http = new Scheme( "http", 80,  PlainSocketFactory.getSocketFactory());
			sr.register(http);			
				
			// Otherwise - throw exception	
			if( !(url.getProtocol().toLowerCase().startsWith("http") || url.getProtocol().toLowerCase().startsWith("https") ) ) {
				log.error("Cannot initialize communication channel, wrong URL to the service is specified: " + url);
				throw new ServiceException("Cannot initialize communication channel, wrong URL to the service is specified: " + url);
			}
			
			PoolingClientConnectionManager cm = new PoolingClientConnectionManager(sr);
			cm.setMaxTotal(10);
			cm.setDefaultMaxPerRoute(10);

			// Add user credentials
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials( new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(clientName, clientPassword));
			
			httpClient = new DefaultHttpClient(cm);
			httpClient.setCredentialsProvider(credsProvider);	
			
			List<String> authpref = new ArrayList<String>();
			authpref.add(AuthPolicy.BASIC);
			httpClient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
			
			// Get proxy configuration
			String proxyHost = System.getProperty(PROXY_HOST);
			String proxyPortString = System.getProperty(PROXY_PORT);
			
			// If proxy is configured try to set it
			if(proxyHost != null && proxyHost.length()>0 && proxyPortString!=null && proxyPortString.length()>0) {				
				try {
					int proxyPort = Integer.parseInt(proxyPortString);
					HttpHost proxy = new HttpHost(proxyHost, proxyPort);
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
					
					// Get Proxy credentials configuration
					String proxyUser = System.getProperty(PROXY_USER);
					String proxyPassword = System.getProperty(PROXY_PASSWORD);
					
					// If proxy authentication is configured , setup user and password for Proxy
					if(proxyUser!=null && proxyUser.length()>0 && proxyPassword!=null && proxyPassword.length()>0) {
						httpClient.getCredentialsProvider().setCredentials(
			                    new AuthScope(proxyHost, proxyPort),
			                    new UsernamePasswordCredentials(proxyUser, proxyPassword));
					}
					
				}catch(Exception e) {
					log.error("Cannot set proxy configuration, host: " + proxyHost + ", port: " + proxyPortString + " because of: " + e.getMessage(), e);
				}
			}
			
			// Create object mapper
			mapper = new ObjectMapper();
			
			connected = true;
			
			log.info("Service started successfully");
			
		} catch (Exception e) {
			log.error("Cannot initialize HTTP service ", e);
			throw new ServiceException("Cannot initialize HTTP service " + e.getMessage());
		}
	}

	@Override
	public void stop() throws ServiceException {
		log.info("Stopping service ...");
		if(httpClient != null)
			httpClient.getConnectionManager().shutdown();
		log.info("Service stopped");
	}

	public void finalize() throws Exception {
		stop();
	}


	
}
