package com.slack.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;

import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.Controller;
import me.ramswaroop.jbot.core.slack.EventType;
import me.ramswaroop.jbot.core.slack.models.Event;
import me.ramswaroop.jbot.core.slack.models.Message;

@Component
public class SlackBot extends Bot {

	@Autowired
	private Conversation conversation;

	@Autowired
	private Translator translator;
	
	/**
	 * Slack token from application.properties file. You can get your slack
	 * token next <a href="https://my.slack.com/services/new/bot">creating a new
	 * bot</a>.
	 */
	@Value("${slackBotToken}")
	private String slackToken;

	@Value("${enableTranslator}")
	private boolean enableTranslator;
	
	@Override
	public String getSlackToken() {
		return slackToken;
	}

	@Override
	public Bot getSlackBot() {
		return this;
	}

	/**
	 * Invoked when the bot receives a direct mention (@botname: message) or a
	 * direct message. NOTE: These two event types are added by jbot to make
	 * your task easier, Slack doesn't have any direct way to determine these
	 * type of events.
	 *
	 * @param session
	 * @param event
	 */
	@Controller(events = { EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE })
	public void onReceiveDM(WebSocketSession session, Event event) {
		// slackService.getCurrentUser().getName()

		System.out.println("Received message from " + event.getUserId());
		
		String userSentence = event.getText();
		
		String lang = "en";
		
		Language from = null;
		
		if (enableTranslator) {
			lang = translator.identify(event.getText());
			
			System.out.println("Message Language: " + lang);
			
			if (!"en".equals(lang)) {
				
				if("fr".equals(lang)) {
					from = Language.FRENCH;
				}
				
				if("it".equals(lang)) {
					from = Language.ITALIAN;
				}
				
				if("ru".equals(lang)) {
					from = Language.RUSSIAN;
				}
				
				if (from != null) {
					userSentence = translator.translate(userSentence, from, Language.ENGLISH);
				}
			}
		}
		
		System.out.println("Final user sentence is " + userSentence);
		
		String message = conversation.sendMessage(event.getUserId(), userSentence).getText().get(0);

		if (enableTranslator) {	
			if (!"en".equals(lang) && from != null) {
				message = translator.translate(message, Language.ENGLISH, from);
			}
		}
		
		reply(session, event, new Message(message));
	}
}