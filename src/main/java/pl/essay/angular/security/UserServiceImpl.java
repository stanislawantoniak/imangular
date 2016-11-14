package pl.essay.angular.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.toolbox.EmailSender;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	protected static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired private UserDao userDao;
	
	@Autowired private EmailSender senderService;
	
	@Override
	public void updateUser(UserT i){
		this.userDao.update(i);
	}

	@Override
	public long addUser(UserT i){
		this.userDao.create(i);
		return i.getId();
	}

	@Override
	public SetWithCountHolder<UserForm> listUsers(){
		//return safe UserForm list - with no passwords
		List<UserForm> users = new LinkedList<UserForm>();

		Set<UserT> rawUsers = this.userDao.getAll().getCollection();
		for (UserT u : rawUsers){
			users.add( new UserForm( u ) );
		}
		return new SetWithCountHolder<UserForm>( users, users.size() );

	}

	@Override
	public UserT getUserById(int id){
		return this.userDao.get(id);
	}

	@Override
	public void removeUser(int id){
		this.userDao.deleteById(id);
	}

	//throws UsernameNotFoundException according to UserDetail contract
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (userDao.existsUserByName(username)){
			logger.debug("user found! "+username);
			return this.userDao.getUserByName(username);
		}
		else
			throw new UsernameNotFoundException("User : "+username +" not found.");
	}

	@Override
	public boolean existsUser(String username){
		return userDao.existsUserByName(username);
	}

	/*
	 * looks up for user
	 * if not fount => return null
	 * 
	 * if found => make a new hash, put it into user, send email and return the hash
	 */
	@Override
	public String getForgotPasswordHashForUser(String userName){
		if (this.userDao.existsUserByName(userName)){

			UUID id = UUID.randomUUID();
			UserT user = this.userDao.getUserByName(userName);
			user.setForgotPasswordHash(id.toString());
			this.userDao.update(user);

			logger.debug("hash generated::"+user.getForgotPasswordHash()+" for user "+user.getUsername());
			
			this.senderService.sendEmail(userName, "hash for user", id.toString());

			return user.getForgotPasswordHash();
		}
		else
			return null;
	}

	/*
	 * looks up for user by forgotPasswordHash
	 * if not fount => return null
	 * 
	 * if found => return user
	 * 
	 * tofix = check whether hash not expired
	 * 
	 */
	@Override
	public UserT getUserByForgotPasswordHash(String hash){

		UserT user = this.userDao.getUserByForgotPasswordHash(hash);
		if (user != null)
			logger.debug("hash found::"+user.getForgotPasswordHash()+" for user "+user.getUsername());
		else
			logger.debug("hash not found::"+hash);

		return user;
	}

}
