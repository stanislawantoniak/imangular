package pl.essay.angular.security;

import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.Dao;

@Transactional
public interface UserDao extends Dao<UserT> {
	public UserT getUserByName(String name);
	public boolean existsUserByName(String name);
}
