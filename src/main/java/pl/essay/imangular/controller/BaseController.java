package pl.essay.imangular.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import pl.essay.angular.security.UserSession;


public class BaseController {
	
	//with scope session
	@Autowired
	protected UserSession userSession;

}
