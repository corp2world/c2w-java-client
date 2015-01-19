package com.c2w.client.core.message;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This class represents user response for dialog message
 */
@XmlRootElement
public class MessageResponse extends PropertiesModel {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Original message ID
	 */
	private long messageId;
	
	/*
	 * Response time
	 */
	private long timestamp;
	
	/*
	 * Responded option
	 */
	private String respondedOption;
	
	/*
	 * Responded user ID
	 */
	private String userId;
	
	/*
	 * Channel ID through which response user responded 
	 */
	private int channelId;

	/**
	 * Create new blank instance
	 */
	public MessageResponse() {
	}
	
	/**
	 * Create new instance with the given parameters
	 * @param messageId
	 * @param timestamp
	 * @param respondedOption
	 * @param userId
	 * @param channelId
	 */
	public MessageResponse(long messageId, long timestamp, String respondedOption, String userId, int channelId) {
		super();
		this.messageId = messageId;
		this.timestamp = timestamp;
		this.respondedOption = respondedOption;
		this.userId = userId;
		this.channelId = channelId;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getRespondedOption() {
		return respondedOption;
	}

	public void setRespondedOption(String respondedOption) {
		this.respondedOption = respondedOption;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	
}
