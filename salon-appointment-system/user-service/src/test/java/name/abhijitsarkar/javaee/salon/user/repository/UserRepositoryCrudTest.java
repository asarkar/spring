package name.abhijitsarkar.javaee.salon.user.repository;

import static java.util.Arrays.asList;
import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;
import name.abhijitsarkar.javaee.salon.test.ContentMatcher;
import name.abhijitsarkar.javaee.salon.test.DeleteAndCompare;
import name.abhijitsarkar.javaee.salon.test.GetAndCompare;
import name.abhijitsarkar.javaee.salon.test.IdExtractor;
import name.abhijitsarkar.javaee.salon.test.Pair;
import name.abhijitsarkar.javaee.salon.test.UpdateAndCompare;
import name.abhijitsarkar.javaee.salon.user.TestUserApp;
import name.abhijitsarkar.javaee.salon.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestUserApp.class)
@WebAppConfiguration
@ActiveProfiles({ "NoReg", "NoAuth" })
public class UserRepositoryCrudTest {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();

	private String jsonUser;

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@PostConstruct
	void init() throws JsonProcessingException {
		mockMvc = webAppContextSetup(webApplicationContext).build();

		User testUser = new User().withFirstName("John").withLastName("Doe").withPhoneNum("111-111-1111");

		jsonUser = OBJECT_MAPPER.writeValueAsString(testUser);
	}

	@Before
	public void setUp() {
		authorityRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	public void testCreateUser() throws Exception {
		Pair pair = new Pair(asList("_links", "self", "href"), ".*/users/\\d+$");

		createNewUser().andExpect(content().string(new ContentMatcher(pair)));
	}

	@Test
	public void testGetUser() throws Exception {
		Pair pair = new Pair(asList("_links", "self", "href"), ".*/users/\\d+$");

		createNewUser().andDo(new GetAndCompare(pair, mockMvc, "/users/%s"));
	}

	@Test
	public void testUpdateUser() throws Exception {
		UserExtractor userExtractor = new UserExtractor("Johnny");
		Pair pair = new Pair(asList("firstName"), "Johnny");

		createNewUser().andDo(new UpdateAndCompare<User>(pair, mockMvc, "/users/%s", userExtractor));
	}

	@Test
	public void testDeleteUser() throws Exception {
		createNewUser().andDo(new DeleteAndCompare(mockMvc, "/users/%s"));
	}

	private ResultActions createNewUser() throws Exception {
		return mockMvc.perform(post("/users").content(jsonUser).contentType(APPLICATION_JSON).accept(HAL_JSON))
				.andExpect(content().contentType(HAL_JSON)).andExpect(status().isCreated());
	}

	private final class UserExtractor implements Function<MvcResult, User> {
		private final String newFirstName;

		private UserExtractor(String newFirstName) {
			this.newFirstName = newFirstName;
		}

		@Override
		public User apply(MvcResult result) {
			try {
				String body = result.getResponse().getContentAsString();
				JsonNode getTree = OBJECT_MAPPER.readTree(body);

				String userId = new IdExtractor().apply(result);
				String lastName = getTree.path("lastName").asText();
				String phoneNum = getTree.path("phoneNum").asText();

				return new User().withId(Long.valueOf(userId)).withFirstName(newFirstName).withLastName(lastName)
						.withPhoneNum(phoneNum);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
