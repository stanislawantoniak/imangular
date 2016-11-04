package pl.essay.angular.security;

import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.GenericDaoHbn;

@Transactional
public interface UserDao extends GenericDaoHbn<UserT> {
	public UserT getUserByName(String name);
	public boolean existsUserByName(String name);
}
