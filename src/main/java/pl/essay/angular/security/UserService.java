package pl.essay.angular.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import pl.essay.generic.dao.SetWithCountHolder;

public interface UserService extends UserDetailsService {

	public long addUser(UserT i);

	public void updateUser(UserT i);

	public SetWithCountHolder<UserForm> listUsers();

	public UserT getUserById(int id);

	public void removeUser(int id);

	public boolean existsUser(String username);

	public String getForgotPasswordHashForUser(String userName);

	public UserT getUserByForgotPasswordHash(String hash);

	public boolean changePassForHash(String hash, String pass);

	public void setDateLastLoggedIn(UserT u);

}
