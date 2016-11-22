package pl.essay.angular.sessioncatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.angular.security.UserForm;
import pl.essay.generic.dao.ListingParamsHolder;
import pl.essay.generic.dao.SetWithCountHolder;

@RestController
public class SessionLogEntryController {
	
	@Autowired 
	private SessionLogEntryService sessionLogEntryService;
	
	@RequestMapping(value = "/sessionlogs", method = RequestMethod.POST)
	@PreAuthorize("hasRole('"+UserForm.roleSupervisor+"')")
	public SetWithCountHolder<SessionLogEntry>  getSessionLogWithParams(@RequestBody ListingParamsHolder filter) {
		return this.sessionLogEntryService.listEntitiesPaginated( filter );
	}

}
