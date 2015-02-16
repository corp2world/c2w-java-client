package com.c2w.client.core.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.c2w.client.core.service.http.HttpService;

/**
 * Unit test for {@link com.c2w.client.core.service.ServiceFactory} class
 * 
 * @author ptrvif
 *
 */
public class ServiceFactoryTest {

	@Before
	public void setUp() {
		Logger.getLogger(ServiceFactory.class.getName()).setLevel(Level.OFF);
	}
	
	@Test
	public void testGetService() {
		
		Service service = ServiceFactory.getService();
		
		assertNotNull(service);
		
		assertTrue(service instanceof HttpService);
	}

}
