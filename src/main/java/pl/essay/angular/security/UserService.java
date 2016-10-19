package pl.essay.angular.security;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{

	    public long addUser(UserT i);
		public void updateUser(UserT i);
		public List<UserForm>  listUsers();
		public UserT getUserById(int id);
		public void removeUser(int id);
		public boolean existsUser(String username);

}
