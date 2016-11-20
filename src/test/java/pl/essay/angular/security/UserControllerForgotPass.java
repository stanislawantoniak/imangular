package pl.essay.angular.security;

import java.nio.charset.Charset;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import pl.essay.angular.security.UserService;


import static org.mockito.Mockito.*;
import org.mockito.runners.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
@RestClientTest(UserController.class)
@TestPropertySource(locations="classpath:test.properties")
public class UserControllerForgotPass {

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
	public void testGetUsernameWhenHashNotExists() throws Exception{

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserT user = UserControllerUpdateUserTests.getUser("123456");
		String hash = "hash";
		when(userServiceMock.getUserByForgotPasswordHash(hash)).thenReturn( null );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = get("/getusername/"+hash)
				.contentType(APPLICATION_JSON_UTF8);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isNotFound())
		.andExpect( content().string("not found"));

	}
	@Test
	public void testGetUsernameWhenHashExists() throws Exception{

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserT user = UserControllerUpdateUserTests.getUser("123456");
		String hash = "hash";
		when(userServiceMock.getUserByForgotPasswordHash(hash)).thenReturn( user );

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = get("/getusername/"+hash)
				.contentType(APPLICATION_JSON_UTF8);

		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk())		
		.andExpect( content().string(user.getUsername())); 
		
		//assertEquals("",1,2);

	}
	
	
}
