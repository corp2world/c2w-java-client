package com.c2w.client.core.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents message delivery data 
 */
public class MessageDelivery implements Serializable {

	/**
	 * Delivery  Status - possible values
	 */
	public static enum DELIVERY_STATUS {DELIVERED_OK, DELIVERED_ERROR, DELIVERED_ERROR_REVIEWED, NOT_DELIVERED_LIMITED, NOT_DELIVERED_LIMITED_REVIEWED, GROUPED_PENDING, GROUPED_REVIEWED, IN_PROGRESS };
	
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;

	private long deliveryId;
	private long messageId;
	private long customerChannelId;
	private DELIVERY_STATUS status;
	private long deliveryTime;
	private String deliveryDateTime;
	private String note;
	private List<String> recipients;
	
	public MessageDelivery() {}
	
	public MessageDelivery(long deliveryId, long messageId, long customerChannelId,
			DELIVERY_STATUS status, long deliveryTime, String note) {
		super();
		this.deliveryId = deliveryId;
		this.messageId = messageId;
		this.customerChannelId = customerChannelId;
		this.status = status;
		this.deliveryTime = deliveryTime;
		this.note = note;
		recipients = new ArrayList<String>(1);
	}


	public MessageDelivery(long deliveryId, long messageId, long customerChannelId,
			DELIVERY_STATUS status, long deliveryTime, String deliveryDateTime, String note) {
		
		this(deliveryId, messageId, customerChannelId, status, deliveryTime, note);
		this.deliveryDateTime = deliveryDateTime;
	}

	
	public long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(long deliveryId) {
		this.deliveryId = deliveryId;
	}

	public long getMessageId() {
		return messageId;
	}
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	public long getCustomerChannelId() {
		return customerChannelId;
	}
	public void setCustomerChannelId(long customerChannelId) {
		this.customerChannelId = customerChannelId;
	}
	public DELIVERY_STATUS getStatus() {
		return status;
	}
	public void setStatus(DELIVERY_STATUS status) {
		this.status = status;
	}
	public long getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public String getDeliveryDateTime() {
		return deliveryDateTime;
	}

	public void setDeliveryDateTime(String deliveryDateTime) {
		this.deliveryDateTime = deliveryDateTime;
	}
	
	/**
	 * Get list of recipients for the given delivery, can be used with channels which support delivery to a group of users,
	 * for example Email channel.
	 */
	public List<String> getRecipients() {
		return recipients;
	}
	
	/**
	 * Set list of recipients
	 * @param recipients
	 */
	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}
	
	
}
