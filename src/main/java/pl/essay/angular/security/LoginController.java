package pl.essay.angular.security;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.imangular.controllers.BaseController;

@RestController
public class LoginController extends BaseController {

	//@Autowired private UserService userService;
	
	@RequestMapping("/userDetails")
	public Map<String,String> user(HttpServletRequest request, Principal user) {
		System.out.println("User details request:\n"+request);
		//initUser();//#toremove
		Map<String,String> simpleUser = new HashMap<String,String>();
		simpleUser.put("name", user.getName());
		System.out.println("from userDetails");
		
		return simpleUser;
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.POST)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getPrincipal().toString();
		if (auth != null){    
			new SecurityContextLogoutHandler().logout(request, response, auth);
			//persistentTokenBasedRememberMeServices.logout(request, response, auth);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "user logged out succesfully, user details "+name;
	}

	//init for dev purposes
	//just to have a user in db in case of db re-creation
	//to fix
	/*
	private void initUser(){
		String name = "stan@wp.pl";
		String pass  = "123456";

		try {
			this.userService.loadUserByUsername(name);
		} catch (UsernameNotFoundException e) {
			System.out.println("User "+name+" does not exist in db.");
			UserT user = new UserT(name,pass,UserForm.roleAdmin,true);
			this.userService.addUser(user);
			System.out.println("User : "+user+" added to db");
		} finally {

		}
	}
	*/
	

}
