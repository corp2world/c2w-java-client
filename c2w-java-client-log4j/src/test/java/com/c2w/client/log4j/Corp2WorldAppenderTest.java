package com.c2w.client.log4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.c2w.client.core.message.Message;
import com.c2w.client.core.service.ServiceException;
import com.c2w.client.core.service.http.HttpService;

/**
 * Unit test case for {@link com.c2w.client.log4j.Corp2WorldAppender}
 * 
 * @author ptrivf
 *
 */
@RunWith(JMockit.class)
public class Corp2WorldAppenderTest {

	
	private String apiToken = "API|111222...";
	
	private String apiKey = "AABBCC0011...";
	
	private String topicPattern = "%-5p message";
	
	private String pattern = "%d %p [%t] %c{10} (%M:%L) - %m%n";
	
	@Mocked
	HttpService service;

	Corp2WorldAppender appender;
	
	@Before
	public void setUp() {
		Logger.getLogger(HttpService.class.getName()).setLevel(Level.OFF);
		appender = new Corp2WorldAppender();
	}
	
	@Test
	public void testActivateOptions() throws ServiceException {
		appender.activateOptions();
		appender.close();
		
		new Verifications() {{
			new HttpService(); times=1;
			service.start(); times=1;
			service.stop(); times=1;
		}};
	}

	@Test
	public void testCorp2WorldAppender() {
		new Corp2WorldAppender();
	}

	@Test
	public void testCorp2WorldAppenderLayout(@Mocked final PatternLayout layout) {
		appender = new Corp2WorldAppender(layout);
		assertEquals(appender.getLayout(), layout);
	}

	@Test
	public void testClose() throws ServiceException {
		appender.activateOptions();
		appender.close();
		
		new Verifications() {{
			service.stop(); times=1;	
		}};
	}

	@Test
	public void testRequiresLayout() {
		assertFalse(appender.requiresLayout());
	}

	@Test
	public void testAppendLoggingEvent() throws ServiceException {

		LoggingEvent event = new LoggingEvent("com.c2w.client.log4j.Corp2WorldAppenderTest", 
				Logger.getLogger(Corp2WorldAppenderTest.class), Level.INFO, "Test INFO message", null);

		appender.setTopicPattern(topicPattern);
		appender.setLayout(new PatternLayout(pattern));

		appender.activateOptions();
		appender.append(event);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		appender.close();
		
		new Verifications() {{
			service.start();
			service.send(withInstanceOf(Message.class));
		}};
		
	}

	@Test
	public void testSetBufferSize() {
		appender.setBufferSize(1000);
	}

	@Test
	public void testSetApiToken() {
		appender.setApiToken(apiToken);
		assertEquals(System.getProperty(HttpService.API_TOKEN), apiToken);
	}

	@Test
	public void testSetApiKey() {
		appender.setApiKey(apiKey);
		assertEquals(System.getProperty(HttpService.API_KEY), apiKey);
	}

	@Test
	public void testSetTopicPattern() {
		appender.setTopicPattern(topicPattern);
	}


}
