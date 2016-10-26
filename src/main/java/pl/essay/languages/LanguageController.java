package pl.essay.languages;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.security.UserSession;

@RestController
public class LanguageController {

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
		System.out.println("language selected: "+lSelected.getName());
		return lSelected.getTranslator().getTranslations();
	}
}
