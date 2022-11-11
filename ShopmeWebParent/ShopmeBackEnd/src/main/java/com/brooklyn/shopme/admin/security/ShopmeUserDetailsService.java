package com.brooklyn.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.brooklyn.shopme.admin.user.UserRepository;
import com.brooklyn.shopme.common.entity.User;

public class ShopmeUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = repository.findByEmail(email);
		if(user != null)
		{
			return new ShopmeUserDetails(user);
		}
		throw new UsernameNotFoundException("Could not find user with email: "
				+ email);
	}

}
