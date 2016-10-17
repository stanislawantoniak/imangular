package pl.essay.angular.security;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired private UserDao userDao;

	public void updateUser(UserT i){
		this.userDao.update(i);
	}
	public long addUser(UserT i){
		this.userDao.create(i);
		return i.getId();
	}
	public List<UserT> listUsers(){
		return this.userDao.getAll();

	}
	public UserT getUserById(int id){
		return this.userDao.get(id);
	}
	public void removeUser(int id){
		this.userDao.deleteById(id);
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (userDao.existsUserByName(username)){
			System.out.println("user found! "+username);
			return this.userDao.getUserByName(username);
		}
		else
			throw new UsernameNotFoundException("User : "+username +" not found.");
	}

	public boolean existsUser(String username){
		return userDao.existsUserByName(username);
	}
}
