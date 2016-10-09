package pl.essay.angular.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import pl.essay.imangular.languages.Language;
import pl.essay.imangular.languages.Languages;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
	private String cookie;
	@Autowired(required=true)
	@Qualifier(value="english")
	private Language languageSelected;
	
	@Autowired(required=true)
	@Qualifier(value="languages")
	private Languages languages;
	
	String name;
		
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
	
	public String getName(){
		return this.name;
	}
	public void setName(String n){
		this.name = n;
	}
	
}
