package com.c2w.client.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * Service factory class.
 * Provides static method to get the instance of the C2W service.
 * 
 * @author peter
 *
 */
public class ServiceFactory {
	
	
	// System property to specify the service class
	public static final String TRANSPORT_CLASS = "com.c2w.service.class";
	
	// Default service class
	public static final String DEFAULT_TRANSPORT_CLASS = "com.c2w.client.core.service.http.HttpService";
	
	// Logger
	private static Log LOG = LogFactory.getLog(ServiceFactory.class);
	
	// Service instance
	private static Service service;
	
	// Create service instance class
	static {
		initialize();
	}
	
	/**
	 * Get instance of the service
	 * @return
	 */
	public static Service getService() {
		return service;	
	}
	
	/**
	 * Create service instance
	 */
	protected static void initialize() {
		
		String tClass = System.getProperty(TRANSPORT_CLASS);
		
		if(tClass==null || tClass.length()<1) {
			LOG.info("Transport class is not set in system properties , will use default: " + DEFAULT_TRANSPORT_CLASS);
			tClass = DEFAULT_TRANSPORT_CLASS;
		}		
		
		try {			
			service = (Service) Class.forName(tClass).getConstructor().newInstance();
		} catch (Exception e) {
			LOG.error("Cannot initialize transport",e);
		} 
		
	}
	
	
}
