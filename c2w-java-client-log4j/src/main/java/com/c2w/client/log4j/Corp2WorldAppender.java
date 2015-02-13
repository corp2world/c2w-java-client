package com.c2w.client.log4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

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
 * 
 * # Corp2World Appender
 * log4j.appender.C2W=com.c2w.client.log4j.Corp2WorldAppender
 * log4j.appender.C2W.ApiToken=<your api token>
 * log4j.appender.C2W.ApiKey=<your api key>
 * log4j.appender.C2W.BufferSize=100
 * log4j.appender.C2W.TopicPattern=Log %-5p Message
 * log4j.appender.C2W.Layout=org.apache.log4j.PatternLayout
 * log4j.appender.C2W.Layout.ConversionPattern=%d %p [%t] %c{10} (%M:%L) - %m%n
 * 
 * }
 * </pre>
 * 
 * @author ptrvif
 *
 */
public class Corp2WorldAppender extends AppenderSkeleton {

	/**
	 * Default queue size
	 */
	public static final int DEFAULT_QUEUE_SIZE = 1000;
	
	/**
	 * Blocking queue to hold events
	 * Serves as an internal buffer for asynchronous message publishing
	 */
	private BlockingQueue<LoggingEvent> eventQueue;
	
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
	private PatternLayout topicLayout;
	
	/**
	 * Create new appender instance
	 */
	public Corp2WorldAppender() {
	}
	
	
	/**
	 * Create new instance with the given layout
	 * @param layout layout
	 */
	public Corp2WorldAppender(Layout layout) {
		this.layout = layout;
	}
	
	
	/**
	 * Release resources
	 */
	@Override
	public void close() {
		
		try {
			
			if(isRunning)
				isRunning = false;
			
			publisherThread.interrupt();
			
			service.stop();
			
		} catch(ServiceException e) {
			LogLog.warn("Error while stopping service: " + e.getMessage());
		}
		
	}

	
	/**
	 * Require layout
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	
	/**
	 * Activate options
	 */
	@Override
	public void activateOptions() {
		
		try {
		
			/*
			 * Initialize Corp2Wrold service
			 */
			getService();
			
			/*
			 * Initialize buffer queue
			 */
			if(eventQueue == null || eventQueue.size() != queueSize)
				eventQueue = new ArrayBlockingQueue<LoggingEvent>(queueSize);
		
			/*
			 * Start publisher thread
			 */
			startPublisherThread();
			
		} catch(ServiceException e) {
			LogLog.error("Cannot initialize Corp2World service. Appender will be disabled.");
		}
	}
	
	
	/**
	 * This method is called by parent 'appender skeleton' if filters are validated
	 * This method will put the logging event into internal buffer queue to publish the message to the 
	 * Corp2World service asynchronously
	 */
	@Override
	protected void append(LoggingEvent loggingEvent) {
		
		/*
		 * If appender is initialized and publisher thread is running
		 */
		if(isRunning) {
			
			/*
			 * Try to offer the event to the queue
			 */
			if(! eventQueue.offer(loggingEvent) ) {
				
				/*
				 * If queue is full , print warning
				 */
				LogLog.warn("The internal logging event buffer queue is full. Most likely the messages are not sent to the Corp2Wrold service. " +
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
	public void setBufferSize(int size) {
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
	 * Set Topic Layout
	 * @param layout layout used to format message topic
	 */
	public void setTopicPattern(String pattern) {
		this.topicLayout = new PatternLayout(pattern);
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
	 * @return
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
	protected void publishMessage(LoggingEvent event) {

		String messageTopic = null;
		if(topicLayout != null)
			messageTopic = topicLayout.format(event);
		else
			messageTopic = topic;
		
		
		StringBuilder messageText = new StringBuilder();
		
		if(layout != null) 
			messageText = new StringBuilder ( layout.format(event) );
		else
			messageText.append(event.getRenderedMessage());
			
		if(layout == null || layout.ignoresThrowable()) {
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				int len = s.length;
				for(int i = 0; i < len; i++) {
					messageText.append(s[i]);
					messageText.append(Layout.LINE_SEP);
				}
			}
		}
		
		try {
			Message message = new Message(messageTopic, messageText.toString());
			getService().send(message);
		}catch(ServiceException e) {
			LogLog.error("Cannot send message to Corp2World service: " + e.getMessage(), e);
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
					LogLog.debug("Internal publisher thread is interrupted");
				}
				
			}
			
			LogLog.debug("Terminating internal publisher thread");
		}
	}
}
