package com.c2w.client.core.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents message delivery data
 *  
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
	
	/**
	 * Create new instance
	 */
	public MessageDelivery() {}
	
	/**
	 * Create new instance
	 * @param deliveryId delivery ID
	 * @param messageId message ID
	 * @param customerChannelId customer channel ID
	 * @param status delivery status
	 * @param deliveryTime delivery timestamp
	 * @param note additional information
	 */
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

	/**
	 * Create new instance
	 * @param deliveryId delivery ID
	 * @param messageId message ID
	 * @param customerChannelId customer channel ID
	 * @param status delivery status
	 * @param deliveryTime delivery timestamp
	 * @param deliveryDateTime delivery date and time in string presentation
	 * @param note additional information
	 */
	public MessageDelivery(long deliveryId, long messageId, long customerChannelId,
			DELIVERY_STATUS status, long deliveryTime, String deliveryDateTime, String note) {
		
		this(deliveryId, messageId, customerChannelId, status, deliveryTime, note);
		this.deliveryDateTime = deliveryDateTime;
	}

	/**
	 * Get delivery ID
	 * @return delivery ID
	 */
	public long getDeliveryId() {
		return deliveryId;
	}
	
	/**
	 * Set delivery ID
	 * (Populated by Corp2World service)
	 * @param deliveryId delivery ID
	 */
	public void setDeliveryId(long deliveryId) {
		this.deliveryId = deliveryId;
	}

	/**
	 * Get message ID
	 * @return message ID
	 */
	public long getMessageId() {
		return messageId;
	}
	
	/**
	 * Set message ID
	 * (Populated by Corp2World service)
	 * @param messageId message ID
	 */
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}
	
	/**
	 * Get customer channel ID
	 * @return customer channel ID
	 */
	public long getCustomerChannelId() {
		return customerChannelId;
	}
	
	/**
	 * Set customer channel ID
	 * (Populated by Corp2World service)
	 * @param customerChannelId
	 */
	public void setCustomerChannelId(long customerChannelId) {
		this.customerChannelId = customerChannelId;
	}
	
	/**
	 * Get delivery status
	 * @return delivery status
	 */
	public DELIVERY_STATUS getStatus() {
		return status;
	}
	
	/**
	 * Set delivery status
	 * (Populated by Corp2World service)
	 * @param status delivery status
	 */
	public void setStatus(DELIVERY_STATUS status) {
		this.status = status;
	}
	
	/**
	 * Get delivery UTC timestamp 
	 * @return delivery UTC timestamp
	 */
	public long getDeliveryTime() {
		return deliveryTime;
	}
	
	/**
	 * Set delivery UTC timestamp
	 * (Populated by Corp2World service)
	 * @param deliveryTime delivery UTC timestamp
	 */
	public void setDeliveryTime(long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	/**
	 * Get delivery note
	 * @return delivery note
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * Set delivery note
	 * (Populated by Corp2World service)
	 * @param note delivery note
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Get delivery date and time in string presentation 
	 * @return date and time in string presentation
	 */
	public String getDeliveryDateTime() {
		return deliveryDateTime;
	}

	/**
	 * Set delivery date and time in string presentation
	 * (Populated by Corp2World service)
	 * @param deliveryDateTime delivery date and time
	 */
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
