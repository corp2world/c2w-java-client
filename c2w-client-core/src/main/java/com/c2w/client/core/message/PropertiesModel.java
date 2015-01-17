package com.c2w.client.core.message;

import java.io.Serializable;
import java.util.Properties;

public class PropertiesModel implements Serializable {

	/**
	 * Serial version 
	 */
	private static final long serialVersionUID = 1L;

	protected Properties properties;
	
	public Properties getProperties() {
		if(properties == null)
			properties = new Properties();
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}	
	
	public String getProperty(String name) {
		return getProperties().getProperty(name);
	}
	
	public String getProperty(String name, String defaultValue) {
		return getProperties().getProperty(name, defaultValue);
	}
}
