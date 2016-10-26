package pl.essay.angular.security;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
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
public class UserControllerDeleteGetUserTests {

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
	public void testDeleteUser_WhenUserExists() throws Exception{
		//user exists

		//assertEquals("0==0", 0,1);

		//this.userServiceMock = mock(UserService.class);

		UserT user = UserControllerUpdateUserTests.getUser("123456");
		int id = user.getId();

		when(userServiceMock.getUserById(id)).thenReturn(user);

		//System.out.println(requestJson);
		
		MockHttpServletRequestBuilder req = delete("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8);

		//System.out.println(APPLICATION_JSON_UTF8);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(controller).build();
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isOk());

	}
	
	@Test
	public void testDeleteUser_WhenUserDoesNotExist() throws Exception{
		//user does not exist - should be returned http.not_found

		//assertEquals("0==0", 0,1);

		int id = 10;

		when(userServiceMock.getUserById(id)).thenReturn(null);

		//System.out.println(requestJson);

		MockHttpServletRequestBuilder req = delete("/userrest/"+id)
				.contentType(APPLICATION_JSON_UTF8);
				//.content(requestJson);

		//System.out.println(APPLICATION_JSON_UTF8);
		
		mockMvc.perform(
				req
				)
		.andExpect(status().isNotFound());

	}

}
