package pl.essay.imangular.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import pl.essay.angular.security.UserSession;


public class BaseController {

	//with scope session
	@Autowired
	protected UserSession userSession;

}
