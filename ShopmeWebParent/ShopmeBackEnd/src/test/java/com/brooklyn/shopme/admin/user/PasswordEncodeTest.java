package com.brooklyn.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncodeTest {
	@Test
	public void testPasswordEncode()
	{
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String rawPasswordString = "thinh2002";
		String encodedPassword = bCryptPasswordEncoder.encode(rawPasswordString);
		
		System.out.println(encodedPassword);
		boolean matches = bCryptPasswordEncoder.matches(rawPasswordString, encodedPassword);
		assertThat(matches).isTrue();
	}
}
