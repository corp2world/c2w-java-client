package com.c2w.client.core.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test case for {@link com.c2w.client.core.message.PropertiesModel} class
 * 
 * @author ptrvif
 *
 */
public class PropertiesModelTest {

	private PropertiesModel model;
	
	private String key = "key";
	
	private String value = "value";
	
	private String defaultValue = "default value";
	
	@Before
	public void setUp() {
		model = new PropertiesModel();
	}
	
	@Test
	public void testGetProperties() {
		Properties props = model.getProperties();
		assertNotNull(props);
	}

	@Test
	public void testSetProperties() {
		Properties props = new Properties();
		props.setProperty(key, value);
		model.setProperties(props);
		assertEquals(model.getProperty(key), value);
	}

	@Test
	public void testGetPropertyString() {
		
		String returnedValue;
		
		// Test default option
		returnedValue = model.getProperty(key);
		assertNull(returnedValue);
		
		// Test pre-set value
		model.getProperties().setProperty(key,  value);
		returnedValue = model.getProperty(key);
		assertEquals(returnedValue, value);
	}

	@Test
	public void testGetPropertyStringString() {
		String returnedValue;
		
		// Test default option
		returnedValue = model.getProperty(key, defaultValue);
		assertEquals(returnedValue, defaultValue);
		
		// Test pre-set value
		model.getProperties().setProperty(key,  value);
		returnedValue = model.getProperty(key, defaultValue);
		assertEquals(returnedValue, value);
	}

}
