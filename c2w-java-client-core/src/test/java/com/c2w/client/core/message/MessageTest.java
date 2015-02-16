package com.c2w.client.core.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test case for {@link com.c2w.client.core.Message}
 * 
 * @author ptrvif
 *
 */
public class MessageTest {

	private String topic = "My Topic";
	private String text = "My Text";
	private Message message;
	
	@Before
	public void setUp() {
		message = new Message(topic, text);
	}
	
	@Test
	public void testMessageStringString() {
		assertEquals(message.getTopic(), topic);
		assertEquals(message.getText(), text);
		assertTrue(message.getTimestamp() > 0);
	}

	@Test
	public void testSetTopic() {
		String newTopic = "New Topic";
		message.setTopic(newTopic);
		assertEquals(message.getTopic(), newTopic);
	}

	@Test
	public void testSetText() {
		String newText = "New Text";
		message.setText(newText);
		assertEquals(message.getText(), newText);
	}

	@Test
	public void testSetTimestamp() {
		long time = System.currentTimeMillis();
		message.setTimestamp(time);
		assertEquals(message.getTimestamp(), time);
	}

	@Test
	public void testSetTtl() {
		long ttl = 600000;
		message.setTtl(ttl);
		assertEquals(message.getTtl(), ttl);
	}

	@Test
	public void testSetDateTime() {
		String dateTime = "2015-01-01 21:21:21";
		message.setDateTime(dateTime);
		assertEquals(message.getDateTime(), dateTime);
	}

	@Test
	public void testSetChannelRecipients() {
		Map<Long, List<String>> map = new HashMap<Long, List<String>>();
		List<String> list = new ArrayList<String>();
		list.add("recipient1@mail.com");
		list.add("recipient2@mail.com");
		map.put(1l, list);
		message.setChannelRecipients(map);
		assertTrue(message.getChannelRecipients()!=null);
		assertTrue(message.getChannelRecipients().get(1l)!=null);
		assertTrue(message.getChannelRecipients().get(1l).contains("recipient1@mail.com"));
		assertTrue(message.getChannelRecipients().get(1l).contains("recipient2@mail.com"));
	}

	@Test
	public void testSetTest() {
		assertTrue(! message.isTest());
		message.setTest(true);
		assertTrue(message.isTest());
	}


	@Test
	public void testSetDialogOptions() {
		List<String> options = new ArrayList<String>();
		options.add("Confirm");
		options.add("Reject");
		message.setDialogOptions(options);
		assertTrue(message.getDialogOptions().contains("Confirm"));
		assertTrue(message.getDialogOptions().contains("Reject"));
	}

	@Test
	public void testAddDialogOption() {
		message.addDialogOption("Confirm");
		message.addDialogOption("Reject");
		assertTrue(message.getDialogOptions().contains("Confirm"));
		assertTrue(message.getDialogOptions().contains("Reject"));
	}

}
