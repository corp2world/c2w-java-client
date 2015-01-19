package com.c2w.client.core;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.c2w.client.core.message.PropertiesModel;


@XmlRootElement
public class Result extends PropertiesModel {

	/**
	 * default serial version
	 */
	private static final long serialVersionUID = 1L;

	@XmlType(name="ResultStatus", namespace="http://c2w.com/result")
	public static enum Status {OK , ERROR};
	
	private Status status;
	private Object response;
	
	
	/**
	 * Default no-args constructor
	 */
	public Result() {}
	
	/**
	 * Constructor with 'status' parameter
	 * @param status
	 */
	public Result(Status status) {
		this();
		this.status = status;
	}

	/**
	 * Constructor with 'status' and 'response'  parameter
	 * @param status
	 * @param response
	 */
	public Result(Status status, Object response) {
		this(status);
		this.response = response;
	}

	/**
	 * Constructor with additional parameters which will be added as 'property' 
	 * @param status
	 * @param response
	 * @param propertyName
	 * @param propertyValue
	 */
	public Result(Status status, Object response, String propertyName, String propertyValue) {
		this(status, response);
		this.getProperties().setProperty(propertyName, propertyValue);
	}
	
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}
}
