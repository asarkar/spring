package name.abhijitsarkar.javaee.salon.appointment.domain;

import static java.util.Arrays.asList;
import static name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory.newObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserSearchResultTest {
	private final ObjectMapper mapper;
	private final UserSearchResult userSearchResult;

	public UserSearchResultTest() {
		mapper = newObjectMapper();

		Link userSelfLink = new Link().withHref("http://hostname/users/1");

		User user = new User().withFirstName("John").withLastName("Doe").withPhoneNum("111-111-1111")
				.withEmbedded(new Embedded()).withSelfLink(userSelfLink);

		Page page = new Page().withSize(20).withTotalElements(1).withTotalPages(1);

		Link userSearchResultSelfLink = new Link()
				.withHref("http://hostname/users/search/findByLastName?lastName=doe{&page,size,sort}")
				.withTemplated(true);

		userSearchResult = new UserSearchResult().withSelfLink(userSearchResultSelfLink)
				.withEmbedded(new Embedded(asList(user))).withPage(page);
	}

	@Test
	public void testSerialization() throws JsonProcessingException {
		mapper.writeValueAsString(userSearchResult);
	}

	@Test
	public void testDeserialization() throws IOException {
		UserSearchResult userSearchResult = mapper.readValue(getClass().getResourceAsStream("/user-search-result.json"),
				UserSearchResult.class);
		
		assertEquals(this.userSearchResult.getPage(), userSearchResult.getPage());
		assertEquals(this.userSearchResult.getLinks(), userSearchResult.getLinks());
		
		User expectedUser = this.userSearchResult.getEmbedded().getUsers().get(0);

		assertFalse(isEmpty(userSearchResult.getEmbedded().getUsers()));

		User actualUser = userSearchResult.getEmbedded().getUsers().get(0);

		assertEquals(expectedUser, actualUser);
	}
}
