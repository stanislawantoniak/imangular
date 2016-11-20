package pl.essay;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class MainController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String mainPage() {

		return "/entry-html/index.html";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage() {

		return "/entry-html/loginpage.html";
	}
	
	@RequestMapping(value = "/changepass/{hash}", method = RequestMethod.GET)
	public String changePassPage() {
		
		return "/entry-html/changepasspage.html";
	}
	
}
