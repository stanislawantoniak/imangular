package pl.essay.angular.security;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.imangular.controller.BaseController;
import pl.essay.imangular.service.BillOfMaterialService;

@RestController
public class LoginController extends BaseController {

	@Autowired 
	UserService userService;

	@Autowired
	BillOfMaterialService bomService;

	//this is protected resource used for authentication
	@RequestMapping(value = "/userDetails", method = RequestMethod.GET)
	public String user(HttpServletRequest request, HttpSession httpSession) {
		print(httpSession);

		//on successfull login usersession.authenticated is false 
		//it will change to true 2 lines below 
		if (!this.userSession.getAuthenticated()) {// move all boms from anonymous to user (change owner on login)
			this.userSession.updateOnAuthentication();
			this.bomService.moveBomsFromAnonymousToUser(this.userSession.getAnonymousSessionId(), this.userSession.getUser());
		}
		
		return this.userSession.toJson();
	}

	void print(HttpSession httpSession){

		System.out.println("httpSession::"+httpSession.toString());
		System.out.println("\thttpSession id::"+httpSession.getId());
		//System.out.println("\thttpSession time::"+httpSession.getCreationTime());
		//System.out.println("\thttpSession last accessed::"+httpSession.getLastAccessedTime());
		//System.out.println("\thttpSession max inact interv::"+httpSession.getMaxInactiveInterval());
		Enumeration<String> attribs = httpSession.getAttributeNames();
		while(attribs.hasMoreElements()){
			String attrib = attribs.nextElement();
			System.out.println("\thttpSession attrib:: "+attrib+" = "+httpSession.getAttribute(attrib));
		}
		System.out.println("anonymous session::"+this.userSession.getAnonymousSessionId());

	}

	//this is unprotected resource used for fetching data for guest user
	@RequestMapping(value = "common/userSession", method = RequestMethod.GET)
	public String getSession(HttpSession httpSession) {

		this.initFirstUser();

		//this will save session id for anonymous user
		this.userSession.setAnonymousSessionId(httpSession.getId());

		print(httpSession);

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
