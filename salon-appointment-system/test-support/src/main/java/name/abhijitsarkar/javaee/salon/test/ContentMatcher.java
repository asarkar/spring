package name.abhijitsarkar.javaee.salon.test;

import java.io.IOException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;

public class ContentMatcher extends BaseMatcher<String> {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();
	private final Pair pair;

	public ContentMatcher(Pair pair) {
		this.pair = pair;
	}

	@Override
	public boolean matches(Object object) {
		if (!(object instanceof String)) {
			return false;
		}

		String content = (String) object;

		try {
			JsonNode tree = OBJECT_MAPPER.readTree(content);

			for (String path : pair.getPaths()) {
				tree = tree.path(path);
			}

			return tree.asText().matches(pair.getRegex());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void describeTo(Description desc) {
		// TODO Auto-generated method stub
	}
}
