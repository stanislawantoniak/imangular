package pl.essay.angular.security;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pl.essay.angular.security.UserController;
import pl.essay.angular.security.UserForm;
import pl.essay.angular.security.UserService;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.runners.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.collection.IsArrayWithSize.*;
import static org.hamcrest.collection.IsCollectionWithSize.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.Is.*;


@RunWith(MockitoJUnitRunner.class)
@RestClientTest(UserController.class)
@TestPropertySource(locations="classpath:test.properties")
public class UserControllerAllGetsTests {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@InjectMocks
	private UserController controller;

	@Mock
	private UserService userServiceMock;

	@Before
	public void setup() 
	{
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testGetUserList() throws Exception{

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserForm uf1 = UserControllerUpdateUserTests.getUserForm("a123456");
		UserForm uf2 = UserControllerUpdateUserTests.getUserForm("b123456");
		UserForm[] ufArray = new UserForm[]{ uf1, uf2};
		List<UserForm> list = Arrays.asList(ufArray);
		
		when(userServiceMock.listUsers( )).thenReturn( list );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = get("/userslistrest/12qs")
				.contentType(APPLICATION_JSON_UTF8);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk())
		//.andExpect(jsonPath("$", arrayWithSize( 2 ) ))
		.andExpect(jsonPath("$", hasSize( 2 ) ))
		.andExpect(jsonPath("$[?(@.password=='a123456')]", hasSize(1) ))
		.andExpect(jsonPath("$[?(@.password=='b123456')]", hasSize(1) ));
		
		//assertEquals("",1,2);

	}
	
	@Test
	public void testGetUser_WhenUserExists() throws Exception{

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserT user = UserControllerUpdateUserTests.getUser("123456");
		int id = user.getId();
		when(userServiceMock.getUserById( id )).thenReturn( user );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = get("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.password", is(""))) //we never send pass 
		.andExpect(jsonPath("$.username", is("janko@wp.pl")));
		
		//assertEquals("",1,2);

	}
	
	@Test
	public void testGetUser_WhenUserDoesNotExist() throws Exception{

		//assertEquals("0==0", 0,1);

		int id = 0;
		when(userServiceMock.getUserById( id )).thenReturn( null );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = get("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.password", is(""))) //userform should be empty
		.andExpect(jsonPath("$.username", is(""))); 
		
		//assertEquals("",1,2);

	}
	@Test
	public void testGetAllRoles() throws Exception{

		//assertEquals("0==0", 0,1);

		MockHttpServletRequestBuilder req = get("/allRoles")
				.contentType(APPLICATION_JSON_UTF8);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$..*", hasSize(2)))
		.andExpect(jsonPath("$[?(@.ROLE_ADMIN)]", hasSize(1)))
		.andExpect(jsonPath("$[?(@.ROLE_USER)]", hasSize(1))); 
		
		//assertEquals("",1,2);

	}	
}
