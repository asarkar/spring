package name.abhijitsarkar.javaee.salon.test;

import static org.springframework.data.rest.webmvc.RestMediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteAndCompare implements ResultHandler {
	private final MockMvc mockMvc;
	private final String idUriFormat;

	@Override
	public void handle(MvcResult createResult) throws Exception {
		String userId = new IdExtractor().apply(createResult);

		mockMvc.perform(delete(String.format(idUriFormat, userId)).accept(HAL_JSON)).andExpect(status().isNoContent())
				.andDo(new ResultHandler() {
					@Override
					public void handle(MvcResult deleteResult) throws Exception {
						mockMvc.perform(get(String.format("/users/%s", userId)).accept(HAL_JSON))
								.andExpect(status().isNotFound());
					}
				});
	}
}