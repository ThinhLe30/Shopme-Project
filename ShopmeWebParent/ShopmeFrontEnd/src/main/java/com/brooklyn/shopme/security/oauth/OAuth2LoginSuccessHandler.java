package com.brooklyn.shopme.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.brooklyn.shopme.common.entity.AuthenticationType;
import com.brooklyn.shopme.common.entity.Customer;
import com.brooklyn.shopme.customer.CustomerService;
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private CustomerService customerService;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User oauth2User=  (CustomerOAuth2User) authentication.getPrincipal();
		Customer customer = customerService.getCustomerByEmai(oauth2User.getEmail());
		if(customer == null){
			customerService.addNewCustomerUponOAuthLogin(oauth2User.getName(),oauth2User.getEmail(), request.getLocale().getCountry());
		}
		else{
			customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
