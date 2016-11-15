package pl.essay.angular.security;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import pl.essay.angular.security.UserController;
import pl.essay.angular.security.UserForm;
import pl.essay.angular.security.UserService;
import pl.essay.angular.security.UserT;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.runners.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@RestClientTest(UserController.class)
public class UserControllerUpdateUserTests {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@InjectMocks
	private UserController controller;

	@Mock
	private UserService userServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testUpdateUser_WhenUserExists() throws Exception{
		//user exists

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserT user = getUser("123456");
		UserForm uf = getUserForm("changed");
		int id = uf.getId();

		when(userServiceMock.getUserById(id)).thenReturn(user);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString( uf );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = put("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk());

		ArgumentCaptor<UserT> userCaptured = ArgumentCaptor.forClass(UserT.class);
		verify(userServiceMock).updateUser( userCaptured.capture() );
		assertEquals("changed", userCaptured.getValue().getPassword());
		assertEquals("bolko@wp.pl", userCaptured.getValue().getUsername());

	}
	
	@Test
	public void testUpdateUser_WhenUserDoesNotExist() throws Exception{
		//user does not exist - should be returned http.not_found

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserForm uf = getUserForm("changed");
		int id = uf.getId();

		when(userServiceMock.getUserById(id)).thenReturn(null);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString( uf );

		//System.out.println(requestJson);

		MockHttpServletRequestBuilder req = put("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson);

		//System.out.println(APPLICATION_JSON_UTF8);

		mockMvc.perform(
				req
				)
		.andExpect(status().isNotFound());

	}

	@Test
	public void testUpdateUser_WhenUserExistsAndInputPassEmpty() throws Exception{
		//user exists but passowrd in put was empty - should not change

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserT user = getUser("123456");
		UserForm uf = getUserForm("");
		int id = uf.getId();

		when(userServiceMock.getUserById(id)).thenReturn(user);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson=ow.writeValueAsString( uf );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = put("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk());

		ArgumentCaptor<UserT> userCaptured = ArgumentCaptor.forClass(UserT.class);
		verify(userServiceMock).updateUser( userCaptured.capture() );
		assertEquals("123456", userCaptured.getValue().getPassword());

	}
	
	public static final UserT getUser(String pass){
		UserT user = new UserT();
		user.setEnabled(true);
		user.setId(12);
		user.setPassword(pass);
		user.setUsername("janko@wp.pl");
		user.setRoles("ADMIN,USER,MEGAUSER");
		return user;
	}

	public static final UserForm getUserForm(String pass){
		UserForm user = new UserForm();
		user.setEnabled(true);
		user.setId(12);
		user.setPassword(pass);
		user.setUsername("bolko@wp.pl");

		List<String> roles = new ArrayList<String>();
		roles = Arrays.asList(new String[]{"ADMIN","USER"});

		user.setRolesSelected( roles );
		return user;
	}

}
