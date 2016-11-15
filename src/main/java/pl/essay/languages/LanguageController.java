package pl.essay.languages;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.security.UserSession;

@RestController
public class LanguageController {
	
	protected static final Logger logger = LoggerFactory.getLogger(LanguageController.class);

	//with scope session
	@Autowired
	protected UserSession userSession;

	@Autowired
	protected Languages languages; 
	
	@RequestMapping(value = "/common/labels", method = RequestMethod.GET )
	public Map<String,String> getLabels() {
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//System.out.println("labels caller pricipal: "+auth.getPrincipal());
		return this.getUserTranslations();
	}
	
	@RequestMapping(value = "/common/languages", method = RequestMethod.GET)
	public List<Language> getLanguages() {
		return this.languages.getLanguages();
	}

	protected Map<String,String> getUserTranslations(){
		Language lSelected = this.userSession.getLanguageSelected();
		logger.trace("language selected: "+lSelected.getName());
		return lSelected.getTranslator().getTranslations();
	}
}
