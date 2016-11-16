package pl.essay.angular.security;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.toolbox.EmailMaker;
import pl.essay.toolbox.EmailSender;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	protected static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired private UserDao userDao;

	@Autowired private EmailSender senderService;

	@Autowired
	@Qualifier("forgetPasswordEmailMaker")
	private EmailMaker forgetPassTemplateService;

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

			UUID hash = UUID.randomUUID();
			UserT user = this.userDao.getUserByName(userName);
			user.setForgotPasswordHash(hash.toString());
			user.setForgotPasswordHashDate(new Date());
			this.userDao.update(user);

			logger.debug("hash generated::"+user.getForgotPasswordHash()+" for user "+user.getUsername());

			Map<String,String> placeholders = new HashMap<String, String>();
			placeholders.put("@username@", userName);
			placeholders.put("@link@", "https://@domain@/changepass/"+hash.toString());

			String emailBody = this.forgetPassTemplateService.getMail(placeholders);

			this.senderService.sendEmail(userName, "hash for user", emailBody);

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
		if (user != null){

			Calendar c = Calendar.getInstance();
			c.setTime(user.getForgotPasswordHashDate());
			c.add(Calendar.DATE, 1);
			Date expires = new Date( c.getTimeInMillis() );

			Date now = new Date();
			if (now.compareTo(expires)  < 0)
				logger.debug("hash found::"+user.getForgotPasswordHash()+" for user "+user.getUsername());
			else
				logger.debug("hash found but expired "+user.getForgotPasswordHash()+" for user "+user.getForgotPasswordHashDate());
		}
		else
			logger.debug("hash not found::"+hash);

		return user;
	}

	/*
	 * looks up for user by forgotPasswordHash
	 *  
	 * if found => change pass and reset hash in user and return true
	 * 
	 * otherwise => return false
	 * 
	 * tofix = check whether hash not expired
	 * 
	 */
	@Override
	public boolean changePassForHash(String hash, String pass){

		UserT user = this.userDao.getUserByForgotPasswordHash(hash);
		if (user != null){
			user.setPassword(pass);
			user.setForgotPasswordHash("");
			Calendar c = Calendar.getInstance();
			c.set(1990,1,1);
			user.setForgotPasswordHashDate( new Date(c.getTimeInMillis()));
			this.userDao.update( user );
			logger.debug("user found::"+user.getForgotPasswordHash()+" for hash "+user.getUsername()+" password changed");
			return true;
		}
		else {
			logger.debug("hash not found::"+hash);
			return false;
		}
	}

}
