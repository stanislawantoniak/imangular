package pl.essay.angular.security;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import pl.essay.angular.security.UserForm;
import pl.essay.angular.security.UserT;

public class UserFormTests {

	private UserT getUser(String pass) {
		UserT user = new UserT();
		user.setEnabled(true);
		user.setId(10);
		user.setPassword(pass);
		user.setUsername("janko@wp.pl");
		user.setRoles("ADMIN,USER,MEGAUSER");
		return user;
	}

	private UserForm getUserForm(String pass) {
		UserForm user = this.getUserFormNoRoles(pass);

		List<String> roles = new ArrayList<String>();
		roles = Arrays.asList(new String[] { "ADMIN", "USER" });

		user.setRolesSelected(roles);
		return user;
	}

	private UserForm getUserFormNoRoles(String pass) {
		UserForm user = new UserForm();
		user.setEnabled(true);
		user.setId(12);
		user.setPassword(pass);
		user.setUsername("bolko@wp.pl");
		return user;
	}

	@Test
	public void testUpdateUser() {

		UserT user = this.getUser("123456");
		UserForm uf = this.getUserForm("changed");

		user = uf.updateUserT(user);

		assertEquals("password is not changed", uf.getPassword(), user.getPassword());
		assertEquals("name not changed", uf.getUsername(), user.getUsername());
		assertEquals("roles length", 2, user.getRolesList().size());

		uf.setPassword("");
		assertEquals("surce password is not empty", "", uf.getPassword());
		user = uf.updateUserT(this.getUser("123456"));
		assertEquals("password changed", "123456", user.getPassword());
		assertTrue("admin role not found", user.getRoles().contains("ADMIN"));
		assertTrue("user role not found", user.getRoles().contains("USER"));
		assertEquals("id", 10, user.getId());

		uf = this.getUserFormNoRoles("ddd");
		user = this.getUser("123456");
		user = uf.updateUserT(user);

		assertFalse("admin role not found", user.getRoles().contains("ADMIN"));

		// assertEquals("0==0", 0,1);
	}

	@Test
	public void testContructor() {

		UserT user = this.getUser("123456");
		UserForm uf = new UserForm(user);

		assertEquals("password", "", uf.getPassword());// we do not sent current password on userform
		assertEquals("name", user.getUsername(), uf.getUsername());

		assertEquals("roles", 3, uf.getRolesSelected().size());

		Map<String, String> roles = listToMap(uf.getRolesSelected());

		assertTrue("admin role not found", roles.containsKey("ADMIN"));
		assertTrue("user role not found", roles.containsKey("USER"));
		assertTrue("user role not found", roles.containsKey("MEGAUSER"));
		assertEquals("id", 10, uf.getId());

		// check if all roles are present
		roles = uf.getAllRoles();
		assertTrue("admin role not found", roles.containsValue("ROLE_ADMIN"));
		assertTrue("super role not found", roles.containsValue("ROLE_SUPERVISOR"));
		assertTrue("user role not found", roles.containsValue("ROLE_USER"));
		assertEquals("all roles", 3, uf.getAllRoles().size());

		// assertEquals("0==0", 0,1);
	}

	private Map<String, String> listToMap(List<String> list) {
		Map<String, String> roles = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++)
			roles.put(list.get(i), "");
		return roles;
	}

}
