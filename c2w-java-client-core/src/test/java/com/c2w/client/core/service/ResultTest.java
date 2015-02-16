package com.c2w.client.core.service;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for {@link com.c2w.client.core.service.Result} class
 * 
 * @author ptrvif
 *
 */
public class ResultTest {

	@Test
	public void testResult() {
		new Result();
	}

	@Test
	public void testResultStatus() {
		Result result = new Result(Result.Status.OK);
		assertEquals(result.getStatus(), Result.Status.OK);
	}

	@Test
	public void testResultStatusObject() {
		
		Object response = new Object();
		Result result = new Result(Result.Status.OK, response);
		assertEquals(result.getStatus(), Result.Status.OK);
		assertEquals(result.getResponse(), response);
	}

	@Test
	public void testResultStatusObjectStringString() {
		Object response = new Object();
		String key = "Message ID";
		String value = "100";
		Result result = new Result(Result.Status.OK, response, key, value);
		assertEquals(result.getStatus(), Result.Status.OK);
		assertEquals(result.getResponse(), response);		
		assertEquals(result.getProperties().getProperty(key), value);
	}

	@Test
	public void testSetStatus() {
		Result result = new Result(Result.Status.OK);
		result.setStatus(Result.Status.ERROR);
		assertEquals(result.getStatus(), Result.Status.ERROR);
	}

	@Test
	public void testSetResponse() {
		Result result = new Result(Result.Status.OK);
		assertNull(result.getResponse());
		Object response = new Object();
		result.setResponse(response);
		assertEquals(result.getResponse(), response);
	}

}
