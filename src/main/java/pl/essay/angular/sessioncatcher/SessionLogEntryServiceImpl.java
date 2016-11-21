package pl.essay.angular.sessioncatcher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.servicefacade.GenericServiceImpl;

@Service
@Transactional
public class SessionLogEntryServiceImpl extends GenericServiceImpl<SessionLogEntry> implements SessionLogEntryService {

}
