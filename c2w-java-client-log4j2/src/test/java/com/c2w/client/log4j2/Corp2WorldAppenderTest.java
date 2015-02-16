package com.c2w.client.log4j2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.ObjectMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.c2w.client.core.service.ServiceException;
import com.c2w.client.core.service.http.HttpService;

/**
 * Unit test case for {@link com.c2w.client.log4j2.Corp2WorldAppender}
 * 
 * @author ptrvif
 *
 */
@RunWith(JMockit.class)
public class Corp2WorldAppenderTest {

	@Mocked
	HttpService service;
	
	private String name = "C2W";
	
	private Corp2WorldAppender appender;
	
	@Before
	public void setUp() {
		Logger.getLogger(HttpService.class.getName()).setLevel(java.util.logging.Level.OFF);
		appender = Corp2WorldAppender.createAppender(name,"true", "100", "API|0011...", "AABBCC...", "%-5p message",
				PatternLayout.newBuilder().withPattern("%d %-5p [%t] %C{2} (%F:%L) - %m%n").build(), null);
	}
	
	
	@Test
	public void testStart() throws ServiceException {
		
		appender.start();
		
		new Verifications() {{
			new HttpService(); times=1;
			service.start(); times = 1;
		}};
		
	}

	@Test
	public void testStop() throws ServiceException {
		appender.start();
		appender.stop();
		
		new Verifications() {{
			service.stop(); times=1;	
		}};
	}

	@Test
	public void testAppend() throws ServiceException {
		
		LogEvent event = Log4jLogEvent.createEvent(
				"com.c2w.client.log4j2.Corp2WorldAppenderTest", null,
				"com.c2w.client.log4j2.Corp2WorldAppenderTest", Level.INFO,
				new ObjectMessage("Test INFO Message"), null, null, null, null, 
				Thread.currentThread().getName(), null, System.currentTimeMillis());
				
		appender.start();
		
		appender.append(event);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		appender.stop();
		
		new Verifications() {{
			service.start(); times=1;
			service.send(withInstanceOf(com.c2w.client.core.message.Message.class)); times=1;
			service.stop(); times = 1;
		}};
		 
	}


	@Test
	public void testCreateAppender() {
		assertEquals(appender.getName(), name);
		assertNotNull( appender.getLayout() );
	
	}

}
