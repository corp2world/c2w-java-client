package com.c2w.client.core.message;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test case for {@link com.c2w.client.core.message.MessageResponse} class
 * 
 * @author ptrvif
 *
 */
public class MessageResponseTest {

	private long id = 100l;
	
	private long time = System.currentTimeMillis();
	
	private String respondedOption = "Confirm";
	
	private String userId = "recipient@mail.com";
	
	private int channelId = 5;
	
	
	@Test
	public void testMessageResponse() {
		new MessageResponse();
	}

	@Test
	public void testMessageResponseLongLongStringStringInt() {
		MessageResponse response = new MessageResponse(id, time, respondedOption, userId, channelId );
		assertEquals(response.getMessageId(), id);
		assertEquals(response.getTimestamp(), time);
		assertEquals(response.getRespondedOption(), respondedOption);
		assertEquals(response.getUserId(), userId);
		assertEquals(response.getChannelId(), channelId);
	}

	@Test
	public void testSetMessageId() {
		MessageResponse response = new MessageResponse();
		response.setMessageId(id);
		assertEquals(response.getMessageId(), id);
	}

	@Test
	public void testSetTimestamp() {
		MessageResponse response = new MessageResponse();
		response.setTimestamp(time);
		assertEquals(response.getTimestamp(), time);
	}

	@Test
	public void testSetRespondedOption() {
		MessageResponse response = new MessageResponse();
		response.setRespondedOption(respondedOption);
		assertEquals(response.getRespondedOption(), respondedOption);
	}

	@Test
	public void testSetUserId() {
		MessageResponse response = new MessageResponse();
		response.setUserId(userId);
		assertEquals(response.getUserId(), userId);
	}

	@Test
	public void testSetChannelId() {
		MessageResponse response = new MessageResponse();
		response.setChannelId(channelId);
		assertEquals(response.getChannelId(), channelId);
	}

}
