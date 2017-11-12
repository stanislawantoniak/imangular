package pl.essay.angular.security;

import java.nio.charset.Charset;

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
public class UserControllerCreateUserTests {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

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
	public void testCreateUser_WhenUserExists() throws Exception {

		// assertEquals("0==0", 0,1);

		UserForm uf = UserControllerUpdateUserTests.getUserForm("123456");

		when(userServiceMock.existsUser(uf.getUsername())).thenReturn(true);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(uf);

		// System.out.println(requestJson);

		MockHttpServletRequestBuilder req = post("/userrest").contentType(APPLICATION_JSON_UTF8).content(requestJson);

		// System.out.println(APPLICATION_JSON_UTF8);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

		mockMvc.perform(req).andExpect(status().isConflict());
	}

	@Test
	public void testCreateUser_WhenUserDoesNotExist() throws Exception {

		UserT user = new UserT();
		UserForm uf = UserControllerUpdateUserTests.getUserForm("changed");

		when(userServiceMock.existsUser(uf.getUsername())).thenReturn(false);

		when(userServiceMock.addUser(user)).thenReturn((long) 109);

		uf.setId(109);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(uf);

		// System.out.println(requestJson);

		MockHttpServletRequestBuilder req = post("/userrest").contentType(APPLICATION_JSON_UTF8).content(requestJson);

		// System.out.println(APPLICATION_JSON_UTF8);

		System.out.println("test in spe");
		mockMvc.perform(req).andExpect(status().isOk());
		// .andExpect(jsonPath("$", equalTo( 109 ) ));

		ArgumentCaptor<UserT> userCaptured = ArgumentCaptor.forClass(UserT.class);
		verify(userServiceMock).addUser(userCaptured.capture());
		assertEquals("changed", userCaptured.getValue().getPassword());
		assertEquals("bolko@wp.pl", userCaptured.getValue().getUsername());
	}

}
