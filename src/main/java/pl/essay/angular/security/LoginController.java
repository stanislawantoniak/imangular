package pl.essay.angular.security;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.sessioncatcher.SessionLogEntry;
import pl.essay.angular.sessioncatcher.SessionLogEntryService;
import pl.essay.generic.controller.BaseController;
import pl.essay.imangular.domain.bom.BillOfMaterialService;

@RestController
public class LoginController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired 
	UserService userService;
	
	@Autowired
	SessionLogEntryService sessionService;

	@Autowired
	BillOfMaterialService bomService;

	//this is protected resource used for authentication
	@RequestMapping(value = "/userDetails", method = RequestMethod.GET)
	public String user( HttpSession httpSession) {
		print(httpSession);
		
				//on successfull login usersession.authenticated is false 
		//it will change to true 2 lines below 
		if (!this.userSession.getAuthenticated()) {// move all boms from anonymous to user (change owner on login)
			
			this.userSession.updateOnAuthentication();
			this.bomService.moveBomsFromAnonymousToUser(this.userSession.getAnonymousSessionId(), this.userSession.getUser());
		}
	
		return this.userSession.toJson();
	}

	/*
	 * for debugging only
	 */
	void print(HttpSession httpSession){

		logger.trace("httpSession::"+httpSession.toString());
		logger.trace("\thttpSession id::"+httpSession.getId());
		//logger.trace("\thttpSession time::"+httpSession.getCreationTime());
		//logger.trace("\thttpSession last accessed::"+httpSession.getLastAccessedTime());
		//logger.trace("\thttpSession max inact interv::"+httpSession.getMaxInactiveInterval());
		Enumeration<String> attribs = httpSession.getAttributeNames();
		while(attribs.hasMoreElements()){
			String attrib = attribs.nextElement();
			logger.trace("\thttpSession attrib:: "+attrib+" = "+httpSession.getAttribute(attrib));
		}
		logger.trace("anonymous session::"+this.userSession.getAnonymousSessionId());

	}

	/*
	 * this is unprotected resource used for fetching data stored in usersession for guest user
	 */
	@RequestMapping(value = "common/userSession", method = RequestMethod.GET)
	public String getSession(HttpServletRequest request, HttpSession httpSession) {

		this.initFirstUser();

		//this will save session id for anonymous user
		this.userSession.setAnonymousSessionId(httpSession.getId());
		
		SessionLogEntry entry = new SessionLogEntry();
		entry.setIp(request.getRemoteAddr());
		entry.setSessionId(this.userSession.getAnonymousSessionId());
		if (this.userSession.getUser() != null)
			entry.setUserId(this.userSession.getUser().getId());
		sessionService.addEntity( entry );

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

		logger.trace("user init in progress");

		try {
			this.userService.loadUserByUsername(name);
		} catch (UsernameNotFoundException e) {
			logger.trace("User "+name+" does not exist in db.");
			UserT user = new UserT(name,pass,UserForm.roleAdmin,true);
			this.userService.addUser(user);
			logger.trace("User : "+user+" added to db");
		} finally {

		}
	}

}
