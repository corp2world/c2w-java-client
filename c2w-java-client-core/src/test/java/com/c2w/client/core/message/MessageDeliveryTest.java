package com.c2w.client.core.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.c2w.client.core.message.MessageDelivery.DELIVERY_STATUS;

/**
 * Unit test case for {@link com.c2w.client.core.message.MessageDelivery}
 * 
 * @author ptrvif
 *
 */
public class MessageDeliveryTest {

	private long deliveryId = 100;
	
	private long messageId = 200;
	
	private long customerChannelId = 300;
	
	private DELIVERY_STATUS status = DELIVERY_STATUS.DELIVERED_OK;
	
	private long deliveryTime = System.currentTimeMillis();
	
	private String deliveryDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));
	
	private String note = "Message delivered successfully";
	
	private List<String> recipients = new ArrayList<String>();
	
	private MessageDelivery delivery;

	@Before
	public void setUp() {
		delivery = new MessageDelivery();
	}
	
	@Test
	public void testMessageDelivery() {
		new MessageDelivery();
	}

	@Test
	public void testMessageDeliveryLongLongLongDELIVERY_STATUSLongString() {
		MessageDelivery delivery = new MessageDelivery(deliveryId, messageId, customerChannelId, status, deliveryTime, note);
		assertEquals(delivery.getDeliveryId() , deliveryId);
		assertEquals(delivery.getMessageId() , messageId);
		assertEquals(delivery.getCustomerChannelId() , customerChannelId);
		assertEquals(delivery.getStatus() , status);
		assertEquals(delivery.getDeliveryTime() , deliveryTime);
		assertEquals(delivery.getNote() , note);
	}

	@Test
	public void testMessageDeliveryLongLongLongDELIVERY_STATUSLongStringString() {
		MessageDelivery delivery = new MessageDelivery(deliveryId, messageId, customerChannelId, status, deliveryTime, 
				deliveryDateTime, note );
		assertEquals(delivery.getDeliveryDateTime(), deliveryDateTime);
	}

	@Test
	public void testSetDeliveryId() {
		delivery.setDeliveryId(deliveryId);
		assertEquals(delivery.getDeliveryId() , deliveryId);
	}

	@Test
	public void testSetMessageId() {
		delivery.setMessageId(messageId);
		assertEquals(delivery.getMessageId() , messageId);
	}

	@Test
	public void testSetCustomerChannelId() {
		delivery.setCustomerChannelId(customerChannelId);
		assertEquals(delivery.getCustomerChannelId() , customerChannelId);
	}

	@Test
	public void testSetStatus() {
		delivery.setStatus(status);
		assertEquals(delivery.getStatus() , status);
	}

	@Test
	public void testSetDeliveryTime() {
		delivery.setDeliveryTime(deliveryTime);
		assertEquals(delivery.getDeliveryTime() , deliveryTime);
	}

	@Test
	public void testSetNote() {
		delivery.setNote(note);
		assertEquals(delivery.getNote() , note);
	}

	@Test
	public void testSetDeliveryDateTime() {
		delivery.setDeliveryDateTime(deliveryDateTime);
		assertEquals(delivery.getDeliveryDateTime(), deliveryDateTime);
	}

	@Test
	public void testSetRecipients() {
		recipients.add("user1@mail.com");
		recipients.add("user2@mail.com");
		delivery.setRecipients(recipients);
		assertEquals(delivery.getRecipients(), recipients);
	}

}
