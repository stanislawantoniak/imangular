package pl.essay.angular.security;

import org.springframework.stereotype.Repository;

import pl.essay.generic.dao.AbstractDaoHbn;

@Repository
public class UserDaoImpl extends AbstractDaoHbn<UserT> implements UserDao {

	public UserT getUserByName(String name) {
		return (UserT) getSession()
				.getNamedQuery("getUserByName") 
				.setParameter("name", name)
				.getSingleResult();
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
