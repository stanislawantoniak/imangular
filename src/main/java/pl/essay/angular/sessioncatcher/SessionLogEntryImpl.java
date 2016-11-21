package pl.essay.angular.sessioncatcher;

import pl.essay.generic.dao.GenericDaoHbnImpl;
import org.springframework.stereotype.Repository;

@Repository
public class SessionLogEntryImpl extends GenericDaoHbnImpl<SessionLogEntry> implements SessionLogEntryDao {

}
