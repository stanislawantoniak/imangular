package pl.essay.angular.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.essay.generic.dao.SetWithCountHolder;
import pl.essay.imangular.controller.BaseController;

@RestController
public class UserController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired private UserService userService;

	//get all users

	@RequestMapping(value = "/userslistrest", method = RequestMethod.GET)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public SetWithCountHolder<UserForm> listUsers() {
		
		SetWithCountHolder<UserForm> theList = this.userService.listUsers();
		logger.trace("user list size: "+theList.getTotalRows());

		return theList;
	}
	
	/*
	 * this service is called on validate edit entity form
	 * - id => id of edited or added user (0 for new entity)
	 * - username -> name entered by user
	 * 
	 * service returns ok if entity is found and its id is different than supplied in request
	 * otherwise it returns http_notfound
	 * 
	 */
	@RequestMapping(value = "/userexists/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> userExists(@RequestBody String username, @PathVariable int id) {
		
		ResponseEntity<Void> notFound = new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		ResponseEntity<Void> found = new ResponseEntity<Void>(HttpStatus.OK);

		if (this.userService.existsUser(username)){
			
			UserT user = (UserT) this.userService.loadUserByUsername(username);
			
			if ( user.getId() != id ){
				logger.trace("user exists "+user);	
				return found;
			}
			else {
				logger.trace("user exists "+username+" but withe the same id as in request" );
				return notFound;
			}
		}
		else {
			logger.trace("user does no exists "+username);
			return notFound;
		}
	}

	
	@RequestMapping(value = "/allRoles", method = RequestMethod.GET)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public Map<String,String> getAllRoles() {
		UserForm uf = new UserForm();
		return uf.getAllRoles();
	}

	@RequestMapping(value = "/userrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<UserForm> getUser(@PathVariable("id") int id) {
		//logger.info("Fetching User with id " + id);
		// init user with new user when requested id = 0, ie we need a form with some predefined data
		// or get from db
		UserForm user = new UserForm( (id != 0 ? this.userService.getUserById(id) : new UserT()) );
		return new ResponseEntity<UserForm>(user, HttpStatus.OK);
	}

	@RequestMapping(value= "/userrest/{id}", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody UserForm userForm){

		logger.info("update userform data: "+userForm);

		UserT user = this.userService.getUserById(id);
		if (user == null){
			logger.info("User "+id+" does not exist, update failed");
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}

		user = userForm.updateUserT(user);
		
		//logger.info("before update user data: "+user);
		this.userService.updateUser( user );
		//logger.info("after update user data: "+user);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/userrest", method = RequestMethod.POST)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<Long> createUser(@RequestBody UserForm userForm){

		logger.info("create userform data: "+userForm);
		
		if ( this.userService.existsUser( userForm.getUsername() ) ){
			logger.info("User with name " + userForm.getUsername() + " already exist and requested create");
	            return new ResponseEntity<Long>(0L, HttpStatus.CONFLICT);
		}
			
		UserT user = new UserT();
		user = userForm.updateUserT(user);
		
		logger.trace("before create user data: "+user);
		long id = this.userService.addUser( user );
		
		logger.trace("user id:: "+id);
		
		return new ResponseEntity<Long>( id, HttpStatus.OK);
	}
	
	/*
	 * method to be used for users to register
	 * only name and password is required
	 * 
	 * on succesful register it sets user as logged in 
	 */

	@RequestMapping(value= "/register", method = RequestMethod.POST)
	public ResponseEntity<Long> createUser(@RequestBody Map<String,String> userForm){
		
		String username = userForm.get("username");
		String password = userForm.get("password");
		
		if ( this.userService.existsUser( username ) ){
			logger.info("User with name " + username + " already exist and requested create");
	            return new ResponseEntity<Long>(0L, HttpStatus.CONFLICT);
		}
			
		UserT user = new UserT();
		user.setUsername(username);
		user.setPassword(password);
		user.setEnabled(true);
		user.setRoles(	UserForm.roleUser );
		
		long id = this.userService.addUser( user );
		
		Authentication auth = 
				  new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		
		return new ResponseEntity<Long>( id, HttpStatus.OK);
	}

	@RequestMapping(value= "/userrest/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('"+UserForm.roleAdmin+"')")
	public ResponseEntity<Void> deleteUser(@PathVariable int id){
	
		UserT user = this.userService.getUserById(id);
		if (user == null){			 
			logger.trace("User " +id+ " does not exist but requested delete");
	        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
		logger.trace("before delete user: "+user);
		this.userService.removeUser(id);

		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@RequestMapping(value= "/forgotpass", method = RequestMethod.PUT)
	public ResponseEntity<Void> sendHash(@RequestBody String username){
		String hash = this.userService.getForgotPasswordHashForUser(username);
		logger.trace("hash generated for user "+username);
		if (hash == null)
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<Void>(HttpStatus.OK);
			
	}
	
	@RequestMapping(value= "/getusername/{hash}", method = RequestMethod.GET)
	public ResponseEntity<String> getUserName(@PathVariable String hash){

		UserT user = this.userService.getUserByForgotPasswordHash(hash);
		
		if (user == null)
			return new ResponseEntity<String>("\"not found\"", HttpStatus.NOT_FOUND );
		else
			return new ResponseEntity<String>( "\""+user.getUsername()+"\"", HttpStatus.OK );
			
	}
	
	/*
	 * changes pass for a given hash 
	 * 
	 *	if user exists for hash then change pass and autenthicate the user
	 * 
	 */
	@RequestMapping(value= "/changepass/{hash}", method = RequestMethod.PUT)
	public ResponseEntity<String> changePassForHash(@PathVariable String hash, @RequestBody String password){

		UserT user = this.userService.getUserByForgotPasswordHash(hash); 
			
		if (user == null) 
			return new ResponseEntity<String>("\"not found\"", HttpStatus.NOT_FOUND );
		else {
			
			this.userService.changePassForHash(hash, password);
				
			Authentication auth = 
					  new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(auth);
		
			return new ResponseEntity<String>( "\"ok\"", HttpStatus.OK );
		}
			
	}
	
}
