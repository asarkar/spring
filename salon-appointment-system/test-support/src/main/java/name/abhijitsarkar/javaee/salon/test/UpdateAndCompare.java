package name.abhijitsarkar.javaee.salon.test;

import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Function;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import name.abhijitsarkar.javaee.salon.domain.ObjectMapperFactory;

public class UpdateAndCompare<T> implements ResultHandler {
	private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.newObjectMapper();
	
	private final Pair pair;
	private final MockMvc mockMvc;
	private final String idUriFormat;
	private final Function<MvcResult, T> entityExtractor;

	public UpdateAndCompare(Pair pair, MockMvc mockMvc, String userIdUriFormat,
			Function<MvcResult, T> entityExtractor) {
		this.pair = pair;
		this.mockMvc = mockMvc;
		this.idUriFormat = userIdUriFormat;
		this.entityExtractor = entityExtractor;
	}

	@Override
	public void handle(MvcResult createResult) throws Exception {
		String userId = new IdExtractor().apply(createResult);

		mockMvc.perform(get(String.format(idUriFormat, userId)).accept(HAL_JSON)).andExpect(status().isOk())
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult getResult) throws Exception {
						T entity = entityExtractor.apply(getResult);

						mockMvc.perform(patch(String.format(idUriFormat, userId))
								.content(OBJECT_MAPPER.writeValueAsString(entity)).accept(HAL_JSON))
								.andExpect(content().contentType(HAL_JSON)).andExpect(status().isOk())
								.andDo(new GetAndCompare(pair, mockMvc, idUriFormat));
					}
				});
	}
}
