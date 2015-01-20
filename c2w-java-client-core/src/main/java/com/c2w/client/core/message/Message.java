package com.c2w.client.core.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class represents C2W Message object.
 * Message has topic, text (main message content) and additional properties.
 */
@XmlRootElement
public class Message extends PropertiesModel {
	
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long customerId;
	private String topic;	
	private String text;	
	private long timestamp;	
	private long ttl;
	private String dateTime;
	private List<MessageDelivery> deliveries;
	private Map<Long, List<String>> channelRecipients = new HashMap<Long, List<String>>(0);
	private long deliveryTime;
	private boolean isTest;
	private String clientIp;
	/*
	 * Dialog options
	 */
	private List<String> dialogOptions = new ArrayList<String>();
	
	/**
	 * Default constructor
	 */
	public Message() {		
		// Setup current UTC time
		timestamp = System.currentTimeMillis();
		// No message expiration by default
		ttl = -1;
	}


	/**
	 * Constructor
	 * @param topic
	 * @param text
	 */
	public Message(String topic, String text) {
		this();
		this.topic = topic;
		this.text = text;
	}

	/**
	 * Get message ID
	 * @return message ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set message ID
	 * Populated by the Corp2World service
	 * @param id message ID
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Get customer ID
	 * @return customer ID
	 */
	public long getCustomerId() {
		return customerId;
	}

	/**
	 * Set customer ID
	 * Populated by the Corp2World service
	 * @param customerId customer ID
	 */
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	
	/**
	 * Get message topic
	 * @return
	 */
	public String getTopic() {
		return topic;
	}
	
	/**
	 * Set message topic
	 * @param topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	/**
	 * Get message text
	 * @return
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Set message text
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Get message creation timestamp
	 * @return creation timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Set message creation timestamp
	 * @param timestamp creation timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Get message 'time-to-live' in seconds
	 * (currently may not be supported by the Corp2World service)
	 * @return message 'time-to-live' period in seconds
	 */
	public long getTtl() {
		return ttl;
	}

	/**
	 * Set message 'time-to-live' in seconds
	 * (currently may not be supported by the Corp2World service)
	 * @param ttl
	 */
	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
	
	/**
	 * Get message creation date and time in string representation
	 * @return creation date and time in string representation
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * Set message creation date and time in string representation
	 * Populated by the Corp2World service
	 * @param dateTime creation date and time in string representation
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Get channel-recipient map, if the user ID is not configured on the customer channel (special mode) then
	 * the message will provide the list of recipients for each channel type. 
	 * @return
	 */
	public Map<Long, List<String>> getChannelRecipients() {
		return channelRecipients;
	}

	/**
	 * This info will be used if customer channel(s) configured in the special mode
	 * to take list of recipients from the message.
	 * @param channelRecipients map where the key is customer channel ID and the value is list of the recipients 
	 * 		or user addresses in terms of the given channel, for example a list of email addresses for Email channel.
	 */
	public void setChannelRecipients(Map<Long, List<String>> channelRecipients) {
		this.channelRecipients = channelRecipients;
	}

	/**
	 * Time when message should be delivered. If set then this time should be used to schedule message delivery 
	 * in the future.
	 * @return
	 */
	public long getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * Can be set to schedule message delivery in the future.
	 * @param deliveryTime timestamp when the message should be delivered
	 */
	public void setDeliveryTime(long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	/**
	 * Get message delivery info. 
	 * @return list of delivery objects - one for each customer channel the message was delivered to.
	 */
	public List<MessageDelivery> getDeliveries() {
		return deliveries;
	}

	/**
	 * Set list if delivery objects.
	 * Populated by the Corp2World service
	 * @param deliveries
	 */
	public void setDeliveries(List<MessageDelivery> deliveries) {
		this.deliveries = deliveries;
	}

	/**
	 * Check if test message or regular message. 
	 * Test message is used to test system/channel settings, message is not sent to the actual channel.
	 * 
	 * @return TRUE if 'test' message (system will not send the message to the actual delivery channel)
	 */
	public boolean isTest() {
		return isTest;
	}

	/**
	 * Set 'test' message flag. 
	 * @param isTest TRUE if test message 
	 */
	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	/**
	 * Get client IP address
	 * @return IP address of the client, published this message
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * Set client IP address.
	 * Populated by the Corp2World service
	 * @param senderIp IP address of the client 
	 */
	public void setClientIp(String senderIp) {
		this.clientIp = senderIp;
	}
	
	
	/**
	 * Get dialog options
	 * @return map where key is response and value is hint
	 */
	public List<String> getDialogOptions() {
		return dialogOptions;
	}

	/**
	 * Set dialog options
	 * @param options map where key is response and value is hint
	 */
	public void setDialogOptions(List<String> dialogOptions) {
		this.dialogOptions = dialogOptions;
	}
	
	/**
	 * Add dialog option
	 * @param response option response
	 * @param hint option hint (description)
	 */
	public void addDialogOption(String option) {  
		dialogOptions.add(option);
	}
	
}
