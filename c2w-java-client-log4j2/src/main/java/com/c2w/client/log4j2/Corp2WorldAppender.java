package com.c2w.client.log4j2;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

import com.c2w.client.core.message.Message;
import com.c2w.client.core.service.Service;
import com.c2w.client.core.service.ServiceException;
import com.c2w.client.core.service.http.HttpService;

/**
 * <p>
 * This class implements Log4j Appender interface and can be used to send logging events 
 * to <a href="https://www.corp2world.com">Corp2World.com</a> service.
 * </p>
 * <p>
 * It uses internal in-memory buffer to send logging events messages asynchronously.
 * </p>
 * <p>
 * If internal buffer is full, all incoming logging events will be ignored. 
 * </p>
 * 
 * Configuration example:
 * <br><br>
 * <pre>
 * {@code
 * <Configuration status="debug" packages="com.c2w.client.log4j2">
 *   ...
 *   <Appenders>
 *     ...
 *     <Corp2World name="C2W"
    	  apiToken="<your api token>"
    	  apiKey="<your api key>"
    	  topicPattern="Log  %-5p Message"
    	  bufferSize="100" >
    	  <PatternLayout pattern="%d %-5p [%t] %C{2} (%F:%L) - %m%n"/>
      </Corp2World>
    </Appenders>
    <Loggers>
	  ...
      <Logger name="com.test" level="info" additivity="false">
    	<AppenderRef ref="C2W"/>
      </Logger>
    </Loggers>  
  </Configuration>    
 * }
 * </pre>
 * 
 * @author ptrvif
 *
 */
@Plugin(name = "Corp2World", category = "Core", elementType = "appender", printObject = false)
public class Corp2WorldAppender extends AbstractAppender {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default queue size
	 */
	public static final int DEFAULT_QUEUE_SIZE = 1000;
	
	/**
	 * Blocking queue to hold events
	 * Serves as an internal buffer for asynchronous message publishing
	 */
	private BlockingQueue<LogEvent> eventQueue;
	
	/**
	 * Queue size
	 */
	private int queueSize = DEFAULT_QUEUE_SIZE;
	
	/**
	 * Corp2World Service instance
	 */
	private Service service;
	
	/**
	 * Internal message publishing thread
	 */
	private Thread publisherThread;
	
	/**
	 * Flag to control publisher thread
	 */
	private boolean isRunning;
	
	/**
	 * Topic text will be used in C2W message
	 */
	private String topic = "Message from Log4j appender";
	
	/**
	 * Topic layout
	 */
	private Layout<? extends Serializable> topicLayout;
	

	
	/**
	 * Create new instance with the given arguments
	 * @param name name
	 * @param filter filter
	 * @param layout layout
	 * @param ignoreExceptions ignore exceptions flag
	 */
	protected Corp2WorldAppender(String name, Filter filter, Layout<? extends Serializable> layout, 
			boolean ignoreExceptions) {
		
		super(name, filter, layout, ignoreExceptions);
	}


	/**
	 * Create new instance with the given arguments
	 * @param name name 
	 * @param filter filter
	 * @param layout layout
	 */
	protected Corp2WorldAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		
		super(name, filter, layout);
	}



	/**
	 * Release resources
	 */
	@Override
	public void stop() {
		
		super.stop();
		
		try {
			
			if(isRunning)
				isRunning = false;
			
			publisherThread.interrupt();
			
			service.stop();
			
		} catch(ServiceException e) {
			LOGGER.warn("Error while stopping service: " + e.getMessage());
		}
		
	}


	
	/**
	 * Activate options
	 */
	@Override
	public void start() {
		
		try {
		
			/*
			 * Initialize Corp2Wrold service
			 */
			getService();
			
			/*
			 * Initialize buffer queue
			 */
			if(eventQueue == null || eventQueue.size() != queueSize)
				eventQueue = new ArrayBlockingQueue<LogEvent>(queueSize);
		
			/*
			 * Start publisher thread
			 */
			startPublisherThread();
			
			super.start();
			
		} catch(ServiceException e) {
			LOGGER.error("Cannot initialize Corp2World service. Appender will be disabled.");
		}
	}
	
	
	/**
	 * This method is called by parent 'appender skeleton' if filters are validated
	 * This method will put the logging event into internal buffer queue to publish the message to the 
	 * Corp2World service asynchronously
	 */
	@Override
	public void append(LogEvent logEvent) {
		
		/*
		 * If appender is initialized and publisher thread is running
		 */
		if(isRunning) {
			
			/*
			 * Try to offer the event to the queue
			 */
			if(! eventQueue.offer(logEvent) ) {
				
				/*
				 * If queue is full , print warning
				 */
				LOGGER.warn("The internal logging event buffer queue is full. Most likely the messages are not sent to the Corp2Wrold service. " +
						"PLese check the appender configuration");
			}
		}
	}
	
	
	/**
	 * Set queue size.
	 * Internal queue size will be limited by the given value and if no space in the queue ,
	 * logging events will be ignored 
	 * @param size queue size
	 */
	public void setQueueSize(int size) {
		queueSize = size;
	}

	
	/**
	 * Set Corp2World API access token.
	 * This value will override the value provided in system properties (if any)
	 * @param token token
	 */
	public void setApiToken(String token) {
		
		System.setProperty(HttpService.CLIENT_NAME, token);
	}
	
	
	/**
	 * Set Corp2World API access key.
	 * This value will override the value provided in system properties (if any)
	 * @param key key
	 */
	public void setApiKey(String key) {
		
		System.setProperty(HttpService.CLIENT_PASSWORD, key);
	}
	
	
	/**
	 * Set Corp2World message topic 
	 * @param topic message topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	
	/**
	 * Get Topic Layout
	 * @return layout used to format the message topic
	 */
	public Layout<? extends Serializable> getTopicLayout() {
		return topicLayout;
	}
	
	
	/**
	 * Set Topic Layout
	 * @param layout layout used to format message topic
	 */
	public void setTopicLayout(Layout<? extends Serializable> layout) {
		this.topicLayout = layout;
	}
	
	
	/**
	 * Start appender
	 */
	protected void startPublisherThread() {
	
		if(!isRunning) {

			publisherThread = new PublisherThread();
			
			publisherThread.setDaemon(true);

			isRunning = true;

			publisherThread.start();
		}
	}
	
	
	/**
	 * Get service instance
	 * @return Corp2World service instance
	 * @throws ServiceException
	 */
	protected Service getService() throws ServiceException {
		
		/*
		 * If service is initialized just return the instance
		 */
		if(service != null)
			return service;
		else
			synchronized(this) {
				if(service == null) {
					service = new HttpService();
					service.start();
				}
			}
		
		return service;
	}
	
	
	/**
	 * This method publishes logging event to Corp2World service
	 * @param event logging event
	 */
	protected void publishMessage(LogEvent event) {

		String messageTopic = null;
		if(topicLayout != null)
			messageTopic = new String( topicLayout.toByteArray(event) );
		else
			messageTopic = topic;
		
		
		StringBuilder messageText = new StringBuilder();
		
		if(getLayout() != null) 
			messageText = new StringBuilder ( new String(getLayout().toByteArray(event) ) );
		else
			messageText.append(event.getMessage().getFormattedMessage());

		
		try {
			Message message = new Message(messageTopic, messageText.toString());
			getService().send(message);
		}catch(ServiceException e) {
			LOGGER.error("Cannot send message to Corp2World service: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 * Internal thread for sending messages to Corp2World service asynchronously
	 *
	 */
	private class PublisherThread extends Thread {

		public void run() {
			
			while(isRunning) {
				
				try {
					publishMessage( eventQueue.take() );
				}catch(InterruptedException e) {
					LOGGER.debug("Internal publisher thread is interrupted");
				}
				
			}
			
			LOGGER.debug("Terminating internal publisher thread");
		}
	}
	
	
	/**
	 * Create new Corp2WorldAppender instance
	 * @param name appender name
	 * @param ignore if ignore exceptions
	 * @param bufferSizeStr internal buffer size as number of log events
	 * @param apiToken Corp2World API access token
	 * @param apiKey Corp2World API access key
	 * @param layout layout
	 * @param filter filter
	 * @return appender instance
	 */
	@PluginFactory
	public static Corp2WorldAppender createAppender(
			@PluginAttribute("name") final String name,
			@PluginAttribute("ignoreExceptions") final String ignore,
			@PluginAttribute("bufferSize") final String bufferSizeStr,
			@PluginAttribute("apiToken") final String apiToken,
			@PluginAttribute("apiKey") final String apiKey,
			@PluginAttribute("topicPattern") final String topicPattern,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter
			) {
		
		/*
		 * Appender name must be specified
		 */
		if (name == null) {
			LOGGER.error("No name provided for FileAppender");
			return null;
		}
		
		/*
		 * Internal queue size , use default if not specified
		 */
		final int queueSize = Integers.parseInt(bufferSizeStr, DEFAULT_QUEUE_SIZE);
		
		/*
		 * If ignore exceptions
		 */
		final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
		
		/*
		 * Create default layout if not specified
		 */
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		
		/*
		 * Create instance and set the properties
		 */
		Corp2WorldAppender appender = new Corp2WorldAppender(name, filter, layout, ignoreExceptions);
		appender.setQueueSize(queueSize);
		appender.setApiToken(apiToken);
		appender.setApiKey(apiKey);
		
		if(topicPattern != null && !"".equals(topicPattern) )
			appender.setTopicLayout( PatternLayout.newBuilder().withPattern(topicPattern).build() );
		
		return appender;
	}
}
