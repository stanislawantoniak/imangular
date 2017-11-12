package pl.essay.languages;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.security.UserSession;

@RestController
public class LanguageController {

	protected static final Logger logger = LoggerFactory.getLogger(LanguageController.class);

	// with scope session
	@Autowired
	protected UserSession userSession;

	@Autowired
	protected Languages languages;

	@Value("${eliczile.domain}")
	private String domain;

	/*
	 * returns translations for the user selected language
	 * 
	 * language is stored in userSession
	 * 
	 * adds domain name to be used in angular
	 * 
	 */
	@RequestMapping(value = "/common/labels", method = RequestMethod.GET)
	public Map<String, String> getLabels() {

		Language lSelected = this.userSession.getLanguageSelected();
		logger.trace("language selected: " + lSelected.getName());

		Map<String, String> translations = lSelected.getTranslator().getTranslations();

		return translations;
	}

	@RequestMapping(value = "/common/languages", method = RequestMethod.GET)
	public List<Language> getLanguages() {
		return this.languages.getLanguages();
	}

}
