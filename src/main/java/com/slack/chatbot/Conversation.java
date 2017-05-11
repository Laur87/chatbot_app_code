package com.slack.chatbot;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

@Component
public class Conversation {

	@Value("${conversationWorkspaceId}")
	private String workspaceId;
	
	@Value("${conversationPassword}")
	private String password;
	
	@Value("${conversationUsername}")
	private String username;

	//TODO: Add limits to contextStorage
	private Map<String, Map<String, Object>> contextStorage = new HashMap<String, Map<String, Object>>();
	
	public MessageResponse sendMessage(String userId, String text) {
		
		System.out.println("User is " + username);
		
		ConversationService service = new ConversationService("2017-04-21");
		
		service.setUsernameAndPassword(username, password);

		MessageRequest newMessage = new MessageRequest.Builder().inputText(text)
				.context(contextStorage.get(userId))
				.build();

		MessageResponse response = service.message(workspaceId, newMessage).execute();

		contextStorage.put(userId, response.getContext());
		
		System.out.println(response);

		return response;
	}
}
