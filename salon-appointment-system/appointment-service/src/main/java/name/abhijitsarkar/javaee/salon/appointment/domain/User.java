package name.abhijitsarkar.javaee.salon.appointment.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Wither
@JsonInclude(Include.NON_EMPTY)
public class User {
	private String firstName;

	private String lastName;

	private String phoneNum;

	@JsonInclude(Include.NON_ABSENT)
	private Optional<String> email = Optional.empty();

	@JsonProperty("_links")
	private Map<String, Link> links = new HashMap<>();

	@JsonProperty("_embedded")
	private Embedded embedded;

	User withSelfLink(Link link) {
		links.put("self", link);

		return this;
	}

	public Link getSelfLink() {
		return links.get("self");
	}

	public Long getUserId() {
		String href = getSelfLink().getHref();

		return Long.parseLong(href.substring(href.lastIndexOf('/') + 1));
	}
}