package pl.essay.angular.security;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import pl.essay.languages.*;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession implements Serializable{
	private String cookie;
	@Autowired(required=true)
	@Qualifier(value="english")
	private Language languageSelected;

	@Autowired(required=true)
	@Qualifier(value="languages")
	private Languages languages;
	
	private String name  = "Guest";

	public UserSession(){
	}

	public void setCookie(String c){
		this.cookie = c;
	}
	public String getCookie(){
		return this.cookie;
	}
	public void setLanguageSelected(String c){
		this.languageSelected = this.languages.getLanguage(c);
	}
	public Language getLanguageSelected(){
		return this.languageSelected;
	}

	public void updateName() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null){

			if (auth.isAuthenticated()){

				Object principal = auth.getPrincipal();
				
				String username;

				if (principal instanceof UserDetails) {
					username = ((UserDetails) principal).getUsername();
				} else {
					username = principal.toString();
				}
				
				this.name = username;
			}
		}
	}
	
	public String toJson(){
		return
				"{"+
				"\"name\":\""+this.name+"\","+
				"\"languageSelectedFlag\":\""+this.languageSelected.getFlag()+"\""+
				"}";
	}

}
