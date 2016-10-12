package pl.essay.imangular.controllers;

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

import pl.essay.angular.security.UserForm;
import pl.essay.angular.security.UserService;
import pl.essay.angular.security.UserT;

@Controller
public class MainController extends BaseController {
	
	@Autowired private UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String dashboard(Model model) {
		return "index.html";
	}

}
