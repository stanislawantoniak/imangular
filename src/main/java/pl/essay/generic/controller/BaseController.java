package pl.essay.generic.controller;

import org.springframework.beans.factory.annotation.Autowired;

import pl.essay.angular.security.UserSession;

public class BaseController {

	// with scope session
	@Autowired
	protected UserSession userSession;

}
