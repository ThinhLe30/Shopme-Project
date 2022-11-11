package com.brooklyn.shopme.security.oauth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomerOAuth2User implements OAuth2User {
	private OAuth2User auth2User;
	

	public CustomerOAuth2User(OAuth2User auth2User) {
		this.auth2User = auth2User;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return auth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return auth2User.getAuthorities();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return auth2User.getAttribute("name");
	}
	public String getFullName(){
		return auth2User.getAttribute("name");
	}
	public String getEmail(){
		return auth2User.getAttribute("email");
	}

}
