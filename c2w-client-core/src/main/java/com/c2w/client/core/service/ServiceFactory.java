package com.c2w.client.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceFactory {

	public static final String TRANSPORT_CLASS = "com.c2w.service.class";
	public static final String DEFAULT_TRANSPORT_CLASS = "com.c2w.client.core.service.http.HttpService";
	
	// Log
	private static Log log = LogFactory.getLog(ServiceFactory.class);
	
	private static Service service;
	
	static {
		initialize();
	}
	
	
	public static Service getService() {
		return service;	
	}
	
	
	protected static void initialize() {
		
		String tClass = System.getProperty(TRANSPORT_CLASS);
		
		if(tClass==null || tClass.length()<1) {
			log.info("Transport class is not set in system properties , will use default: " + DEFAULT_TRANSPORT_CLASS);
			tClass = DEFAULT_TRANSPORT_CLASS;
		}		
		
		try {			
			service = (Service) Class.forName(tClass).getConstructor().newInstance();
		} catch (Exception e) {
			log.error("Cannot initialize transport",e);
		} 
		
	}
	
	
}
