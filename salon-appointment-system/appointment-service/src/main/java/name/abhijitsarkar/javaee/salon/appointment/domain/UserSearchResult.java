package name.abhijitsarkar.javaee.salon.appointment.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Wither
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSearchResult {
	@JsonProperty("_links")
	private Map<String, Link> links = new HashMap<>();

	@JsonProperty("_embedded")
	private Embedded embedded = new Embedded();

	private Page page = new Page();

	public UserSearchResult withSelfLink(Link link) {
		links.put("self", link);

		return this;
	}

	public Link getSelfLink() {
		return links.get("self");
	}
}
