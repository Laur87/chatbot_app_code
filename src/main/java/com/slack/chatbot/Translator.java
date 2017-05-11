package com.slack.chatbot;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.IdentifiedLanguage;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;

@Component
public class Translator {

	@Value("${translatorUsername}")
	private String username;
	
	@Value("${translatorPassword}")
	private String password;

	public String identify(String text) {
		System.out.println("Language identification for " + text);

		LanguageTranslator service = new LanguageTranslator();

		service.setUsernameAndPassword(username, password);

		List<IdentifiedLanguage> result = service.identify(text).execute();

		return result.get(0).getLanguage();
	}
	
	public String translate(String text, Language from, Language to) {
		System.out.println("Translating " + text);

		LanguageTranslator service = new LanguageTranslator();

		service.setUsernameAndPassword(username, password);

		TranslationResult result = service.translate(text, from, to).execute();

		return result.getFirstTranslation();
	}
}
