package pl.essay.angular.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginPageController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {
		return "/js/login/login.html";
	}
	
	@RequestMapping(value = "/changepass/{hash}", method = RequestMethod.GET)
	public String changePass() {
		
		//tofix check if hash valid
		
		return "/js/login/changepass.html";
	}
	
}
