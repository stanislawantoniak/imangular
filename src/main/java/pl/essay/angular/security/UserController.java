package pl.essay.angular.security;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.essay.imangular.controller.BaseController;

@RestController
public class UserController extends BaseController {

	protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired private UserService userService;

	//get all users

	@RequestMapping(value = "userslistrest/12qs", method = RequestMethod.GET)
	public List<UserT> listUsers() {
		List<UserT> theList = (List<UserT>) this.userService.listUsers();
		logger.info("list size: "+theList.size());

		return theList;
	}

	@RequestMapping(value = "/userrest/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserForm> getUser(@PathVariable("id") int id) {
		System.out.println("Fetching User with id " + id);
		UserForm user = new UserForm( (id != 0 ? this.userService.getUserById(id) : new UserT()) );//init user for with new user or get from db 
		return new ResponseEntity<UserForm>(user, HttpStatus.OK);
	}

	@RequestMapping(value= "/userrest/{id}", method = RequestMethod.PUT)
	public ResponseEntity<UserForm> updateUser(@PathVariable int id, @RequestBody UserForm userForm){

		logger.info("update userform data: "+userForm);

		UserT user = this.userService.getUserById(id);
		if (user == null){
			System.out.println("User "+id+" does not exist, update failed");
			return new ResponseEntity<UserForm>(HttpStatus.NOT_FOUND);
		}

		user = userForm.updateUserT(user);
		
		logger.info("before update user data: "+user);
		this.userService.updateUser( user );
		logger.info("after update user data: "+user);
		
		return new ResponseEntity<UserForm>(HttpStatus.OK);
	}

	@RequestMapping(value= "/userrest", method = RequestMethod.POST)
	public ResponseEntity<Void> createUser(@RequestBody UserForm userForm){

		logger.info("create userform data: "+userForm);
		
		if ( this.userService.existsUser( userForm.getUsername() ) ){
			 System.out.println("User with name " + userForm.getUsername() + " already exist and requested create");
	            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
			
		UserT user = new UserT();
		user = userForm.updateUserT(user);
		
		logger.info("before create user data: "+user);
		this.userService.addUser( user );
		logger.info("after create user data: "+user);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value= "/userrest/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteUser(@PathVariable int id){
	
		UserT user = this.userService.getUserById(id);
		if (user == null){			 
			System.out.println("User " +id+ " does not exist but requested delete");
	        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
		logger.info("before delete user: "+user);
		this.userService.removeUser(id);
		logger.info("after delete user: "+user);
		
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
