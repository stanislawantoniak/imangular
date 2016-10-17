package pl.essay.imangular.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import pl.essay.angular.security.UserSession;
import pl.essay.languages.Language;
import pl.essay.languages.Languages;

public class BaseController {

	//with scope session
	@Autowired
	protected UserSession userSession;

	@Autowired
	protected Languages languages; 

	protected Map<String,String> getUserTranslations(){
		Language lSelected = this.userSession.getLanguageSelected();
		System.out.println("language selected: "+lSelected.getName());
		return lSelected.getTranslator().getTranslations();
	}

}
