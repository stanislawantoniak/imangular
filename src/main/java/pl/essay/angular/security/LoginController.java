package pl.essay.angular.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pl.essay.imangular.controllers.BaseController;

@Controller
public class LoginController extends BaseController {


	@Autowired private UserService userService;

	/**
	 * This method handles login GET requests.
	 * If users is already logged-in and tries to goto login page again, will be redirected to list page.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage(Model model, HttpServletRequest hsr) {

		this.initUser();//to fix

		this.addGenericDataToModel(model);

		//System.out.println("username: "+this.getUsername());

		//if ("".equals(this.getUsername() )) {
		//    return "login";
		//} else {
		//return "redirect:/";  
		//}

		return "login";
	}


	//init for dev purposes
	//just to have a user in db in case of db re-creation
	//to fix
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
			/*
			User u = (User) this.userService.loadUserByUsername(name);
			u.setPassword(pass);
			u.setEnabled(true);
			u.setRoles(UserForm.roleAdmin);
			this.userService.updateUser(u);
			*/
		}
	}

	/**
	 * This method handles logout requests.
	 * Toggle the handlers if you are RememberMe functionality is useless in your app.
	 */
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response, Model model){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			new SecurityContextLogoutHandler().logout(request, response, auth);
			//persistentTokenBasedRememberMeServices.logout(request, response, auth);
			SecurityContextHolder.getContext().setAuthentication(null);
		}


		this.addGenericDataToModel(model);
		return "redirect:/login?logout";
	}

	/* This method returns true if users is already authenticated [logged-in], else false.

    private boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return this.authenticationTrustResolver.isAnonymous(authentication);
    }
	 */ 
}
