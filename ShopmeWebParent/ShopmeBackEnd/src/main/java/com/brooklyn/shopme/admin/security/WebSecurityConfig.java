package com.brooklyn.shopme.admin.security;

import org.aspectj.weaver.ast.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public UserDetailsService userDetailsService(){
		return new ShopmeUserDetailsService();
	}
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	public DaoAuthenticationProvider authenticationProvider(){
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests()
			.antMatchers("/users/**").hasAuthority("Admin")
			.antMatchers("/categories/**").hasAnyAuthority("Admin","Editor")
			.antMatchers("/brands/**").hasAnyAuthority("Admin","Editor")
			.antMatchers("/products/new","/products/delete/**")
				.hasAnyAuthority("Admin","Editor")
			.antMatchers("/products/edit/**","/products/save","/products/check_unique")
				.hasAnyAuthority("Admin","Editor","Salesperson")
			.antMatchers("/products","/products/","products/detail/**","/products/page/**")
				.hasAnyAuthority("Admin","Editor","Salesperson","Shipper")
			.antMatchers("/products/**").hasAnyAuthority("Admin","Editor")
			.anyRequest().authenticated()
			.and()
				.formLogin()
					.loginPage("/login")
					.defaultSuccessUrl("/",true)
					.usernameParameter("email")
					.permitAll()
			.and()
				.logout().permitAll()
			.and()
				.rememberMe().key("sdsdfdfjkfsdknfksdnfksdnfskfsnf")
				.tokenValiditySeconds(7*24*3600);
			
			
	}
	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		web.ignoring().antMatchers("/images/**","/js/**","/webjars/**");
		
		
	}
	
}
