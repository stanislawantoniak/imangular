package pl.essay.angular.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import pl.essay.generic.dao.CriteriaBuilder;
import pl.essay.generic.dao.GenericDaoHbnImpl;

@Repository
public class UserDaoImpl extends GenericDaoHbnImpl<UserT> implements UserDao {
	
	protected static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
	
	public UserT getUserByName(String name) {

		CriteriaBuilder<UserT> criteriaBuilder = this.getCriteriaBuilder();
		criteriaBuilder.addStrictMatchingFilter("username", name);
		
		UserT user = (UserT) 
				criteriaBuilder
				.get()
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
			logger.debug(e.getLocalizedMessage());
			return false;
		}
		return true;
	}	


	public UserT getUserByForgotPasswordHash(String hash){
		
			
		CriteriaBuilder<UserT> criteriaBuilder = this.getCriteriaBuilder();
		criteriaBuilder.addStrictMatchingFilter("forgotPasswordHash", hash);
		
		return (UserT) 
				criteriaBuilder
				.get()
				.uniqueResult();
	}

}
