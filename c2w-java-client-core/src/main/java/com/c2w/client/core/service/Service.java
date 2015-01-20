package com.c2w.client.core.service;

import java.util.List;

import com.c2w.client.core.message.Message;
import com.c2w.client.core.message.MessageResponse;

/**
 * Interface representing Corp2World client message service
 *
 */
public interface Service {

	/**
	 * Initialize and start service. This method should be called before using service to send messages
	 * @throws ServiceException
	 */
	public void start() throws ServiceException;
	
	/**
	 * Send message to Corp2World service
	 * @param message message object to be sent
	 * @return result 
	 * @throws ServiceException exception is thrown if message could not be sent for any reason
	 */
	public Result send(Message message) throws ServiceException;
	
	/**
	 * Wait for response for the given message ID
	 * @param messageId messageId message ID for which response is needed
	 * @param timeoutSec timeout in seconds for how long to wait
	 * @return message response
	 * @throws ServiceException
	 */
	public List<MessageResponse> waitForResponse(long messageId, long timeoutSec) throws ServiceException;
	
	/**
	 * Stop service and release all resources
	 * @throws ServiceException 
	 */
	public void stop() throws ServiceException; 
}
