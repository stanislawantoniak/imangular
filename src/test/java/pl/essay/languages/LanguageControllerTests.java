package pl.essay.languages;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pl.essay.angular.security.UserSession;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.mockito.Mockito.*;
import org.mockito.runners.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@RestClientTest(LanguageController.class)
public class LanguageControllerTests {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@InjectMocks
	private LanguageController controller;

	@Mock
	protected UserSession userSession;

	@Mock
	protected Languages languages;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testGetLanguages() throws Exception {

		// assertEquals("0==0", 0,1);

		Language l1 = new Language();
		l1.setAcronym("cn");
		l1.setName("chinese");
		Language l2 = new Language();
		l2.setAcronym("pl");
		l2.setName("polski");

		List<Language> lang = Arrays.asList(new Language[] { l1, l2 });

		when(languages.getLanguages()).thenReturn(lang);

		// System.out.println(APPLICATION_JSON_UTF8);

		MockHttpServletRequestBuilder req = get("/common/languages").contentType(APPLICATION_JSON_UTF8);

		mockMvc.perform(req).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[?(@.name=='chinese')]", hasSize(1)))
				.andExpect(jsonPath("$[?(@.name=='polski')]", hasSize(1)));

	}

	@Test
	public void testGetLabels() throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value1");
		map.put("key3", "value3");

		Language l1 = new Language();
		l1.setAcronym("cn");
		l1.setName("chinese");

		Translator t = new Translator();
		t.setTranslations(map);

		l1.setTranslator(t);

		when(userSession.getLanguageSelected()).thenReturn(l1);

		// System.out.println(APPLICATION_JSON_UTF8);

		MockHttpServletRequestBuilder req = get("/common/labels").contentType(APPLICATION_JSON_UTF8);

		mockMvc.perform(req).andExpect(status().isOk()).andExpect(jsonPath("$..*", hasSize(3)))
				.andExpect(jsonPath("$[?(@.key1)]", hasSize(1)))
				.andExpect(jsonPath("$[?(@.key1=='value1')]", hasSize(1)));

	}

}
