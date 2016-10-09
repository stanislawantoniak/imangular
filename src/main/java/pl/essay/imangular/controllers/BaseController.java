package pl.essay.imangular.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import pl.essay.angular.security.UserSession;
import pl.essay.imangular.languages.Language;
import pl.essay.imangular.languages.Languages;

@Controller
public class BaseController {

	//with scope session
	@Autowired
	protected UserSession userSession;

	@Autowired
	protected Languages languages; 
	
	public void setUserSession(UserSession us){
		this.userSession = us;
	}
	public UserSession getUserSession(){
		return this.userSession;
	}

	protected void addGenericDataToModel(Model model){
		this.userSession.setName(this.getUsername());
		model.addAttribute("sessionUser", this.userSession);//basic user data including selected language
		Language lSelected = this.userSession.getLanguageSelected();
		System.out.println("language selected: "+lSelected.getName());
		model.addAttribute("languages", this.languages.getLanguages());//languages for selector
		model.addAttribute("__static__", lSelected.getTranslator().getTranslations());//translations of static elements
		
		//to fix
		//probably not the best place to define classes for view elements
		model.addAttribute("buttonDefaultClasses","btn btn-default btn-sm");
		model.addAttribute("buttonOutlineClasses","btn  btn-outline btn-primary");
		
	}

	protected String getUsername() {
		String username = "";

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null)
			
			if (auth.isAuthenticated()){

				Object principal = auth.getPrincipal();

				if (principal instanceof UserDetails) {
					username = ((UserDetails) principal).getUsername();
				} else {
					username = principal.toString();
				}
			}
    	//System.out.println("username: "+username);
		return username;
	}

}
