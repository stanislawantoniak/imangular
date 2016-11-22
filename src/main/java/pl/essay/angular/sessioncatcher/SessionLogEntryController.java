package pl.essay.angular.sessioncatcher;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.generic.controller.GenericRestControllerImpl;
import pl.essay.generic.servicefacade.GenericServiceImpl;

@RestController
public class SessionLogEntryController extends GenericRestControllerImpl<SessionLogEntry, GenericServiceImpl<SessionLogEntry>>{
	
	@RequestMapping(value = "/sessionlog/", method = RequestMethod.GET)
	
	public String  kick() {
		return this.getDomainClassName();
	}


}
