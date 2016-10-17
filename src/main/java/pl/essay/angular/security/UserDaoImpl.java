package pl.essay.angular.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import pl.essay.generic.dao.AbstractDaoHbn;

@Repository
public class UserDaoImpl extends AbstractDaoHbn<UserT> implements UserDao {

	public UserT getUserByName(String name) {
		UserT user = (UserT) getSession()
				.getNamedQuery("getUserByName") 
				.setParameter("name", name)
				.uniqueResult();
		if (user == null)
			throw (new UsernameNotFoundException("User "+name+" not found."));
		else 
			return user;
	}

	public boolean existsUserByName(String name) {

		try {
			this.getUserByName(name);
		} catch ( Exception e ) {
			System.out.println(e.getLocalizedMessage());
			return false;
		}
		return true;
	}	

}
