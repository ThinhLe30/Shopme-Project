package com.brooklyn.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.brooklyn.shopme.common.entity.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper objectMapper;
	
	@Test
	@WithMockUser(username = "thinhmnsd2002@gmail.com", password = "thinhmnsd2002", roles = "ADMIN")
	public void listCountries() throws Exception{
		String url = "/countries/list";
		MvcResult result = mockMvc.perform(get(url))
		.andExpect(status().isOk())
		.andReturn();
		String jsonResposne = result.getResponse().getContentAsString();
		Country[] countries = objectMapper.readValue(jsonResposne, Country[].class);
		assertThat(countries).hasSizeGreaterThan(0);
	}
	@Test
	@WithMockUser(username = "thinhmnsd2002@gmail.com", password = "thinhmnsd2002", roles = "ADMIN")
	public void tesstCreateUser() throws JsonProcessingException, Exception{
		String url = "/countries/save";
		String countryName = "Canada";
		String countryCode = "CAN";
		Country country = new Country(countryName, countryCode);
		MvcResult result = mockMvc.perform(post(url).contentType("application/json").content(objectMapper.writeValueAsString(country)).with(csrf()))
		.andDo(print())
		.andExpect(status().isOk())
		.andReturn();
		String res = result.getResponse().getContentAsString();
		System.out.println(res);
	}
}
