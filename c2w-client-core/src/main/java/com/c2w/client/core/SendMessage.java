package com.c2w.client.core;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.c2w.client.core.message.Message;
import com.c2w.client.core.message.MessageResponse;
import com.c2w.client.core.service.Service;
import com.c2w.client.core.service.ServiceException;
import com.c2w.client.core.service.ServiceFactory;

/**
 * This class can be used as a simple Java console (command line) application to send message through Corp2World message service.
 * Two arguments are required: 
 * <ul>
 *  <li>message topic</li>
 *  <li>message text</li> 
 * </ul>
 * 
 *
 */
public class SendMessage {

	/**
	 * Two arguments are required: 
	 * <ul>
	 *  <li>message topic</li>
	 *  <li>message text</li> 
	 * </ul>
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length < 2) {
			System.out.println("Two arguments are required: message topic and message test");
			System.exit(0);
		}
		
		// Create message 
		Message message = new Message(args[0], args[1]); 
		
		
		// If message has dynamic recipients
		// in format of <channel_type_id>=[<recipient1>,<recipient2>...]
		// or properties
		// in format of <p_propertyName>=<propertyValue>
		// parse them and assign to message
		for(int i = 2; i < args.length; i++) {
		
			if(args[i].contains("=")) {
			
				String[] parsed = args[i].split("=");
				
				if(parsed[0].indexOf("p_")==0) {
					// Try to parse property name
					String propertyName = parsed[0].substring(2);
					String propertyValue = parsed[1];
					message.getProperties().setProperty(propertyName, propertyValue);
				}				
				else {
					// Try to parse channel type ID
					long channelTypeId;
					try {
						channelTypeId = Long.parseLong(parsed[0]);
					} catch(Exception e) {
						System.out.println("Cannot parse channel type ID: " + parsed[0]);
						continue;
					}
					
					// Try to parse list of recipients
					String[] recipients = parsed[1].split(",");
					
					// If there are recipients , add them to the message
					if(recipients.length > 0)
						message.getChannelRecipients().put(channelTypeId, Arrays.asList(recipients));
				}
			}
			// If test message
			else if(args[i].toLowerCase().equals("test")) {
				message.setTest(true);
			}
			// If dialog option
			else if(args[i].startsWith("d_")) {
				message.addDialogOption(args[i].substring(2));
			}
			
		}
		
		// Get instance of the service
		Service service = ServiceFactory.getService();
		
		try {
			
			// Start service
			System.out.println("Initializing service");
			service.start();
			
			// Send message
			System.out.println("Sending message, topic: " + message.getTopic() + " , text: " + message.getText());
			
			Result result = service.send(message);
			
			System.out.println("Message sent with result: "+result.getStatus());
			System.out.println("Response: "+result.getResponse());
			System.out.println("Message ID: "+result.getProperty("messageId"));
			
			// Wait for response
			if(message.getDialogOptions()!=null && message.getDialogOptions().size()>0) {
				System.out.println("Waiting for response...");
				List<MessageResponse> responseList = service.waitForResponse(Long.parseLong(result.getProperty("messageId")), 600);
				if(responseList!=null && responseList.size()>0) {
					for(MessageResponse response : responseList) {
						System.out.println("Response : " + response.getRespondedOption() + 
								" from " + response.getUserId() + 
								" at " + new Date(response.getTimestamp()).toString());
					}
				}
				else {
					System.out.println("No response, aborting.");
				}
			}
			
			
		} catch (ServiceException e) {
			// Report error
			System.out.println("Error happened");
			e.printStackTrace();
			
		} finally {
			
			// Stop service silently
			System.out.println("Stopping service");
			try {
				service.stop();
			} catch (ServiceException e) {}
			
			System.out.println("Exiting");
		}
	}

}
