package name.abhijitsarkar.javaee.salon.test;

import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAndCompare implements ResultHandler {
	private final Pair pair;
	private final MockMvc mockMvc;
	private final String idUriFormat;

	@Override
	public void handle(MvcResult result) throws Exception {
		String id = new IdExtractor().apply(result);

		mockMvc.perform(get(String.format(idUriFormat, id)).accept(HAL_JSON))
				.andExpect(content().contentType(HAL_JSON)).andExpect(status().isOk())
				.andExpect(content().string(new ContentMatcher(pair)));
	}
}