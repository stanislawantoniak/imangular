package pl.essay.angular.security;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.imangular.controller.BaseController;

@RestController
public class LoginController extends BaseController {
	
	@Autowired 
	UserService userService;

	//this is protected resource used for authentication
	@RequestMapping(value = "/userDetails", method = RequestMethod.GET)
	public String user(HttpServletRequest request, Principal user) {
		
		return this.userSession.toJson();
	}
	
	//this is unprotected resource used for fetching data for guest user
	@RequestMapping(value = "common/userSession", method = RequestMethod.GET)
	public String getSession() {
		
		this.initFirstUser();
		
		return this.userSession.toJson();
	}
	
	@RequestMapping(value = "common/selectLanguage/{languageSelected}", method = RequestMethod.GET)
	public String selectLanguage(@PathVariable String languageSelected) {
		this.userSession.setLanguageSelected(languageSelected);
		return this.userSession.toJson();
	}
	
	//init first user for dev purposes
	//just to have a user in db in case of db re-creation
	
	private void initFirstUser(){
		String name = "stan@wp.pl";
		String pass  = "123456";
		
		System.out.println("user init in progress");

		try {
			this.userService.loadUserByUsername(name);
		} catch (UsernameNotFoundException e) {
			//System.out.println("User "+name+" does not exist in db.");
			UserT user = new UserT(name,pass,UserForm.roleAdmin,true);
			this.userService.addUser(user);
			//System.out.println("User : "+user+" added to db");
		} finally {

		}
	}
	
	

}
