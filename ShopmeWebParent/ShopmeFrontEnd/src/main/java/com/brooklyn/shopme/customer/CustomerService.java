package com.brooklyn.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brooklyn.shopme.common.entity.AuthenticationType;
import com.brooklyn.shopme.common.entity.Country;
import com.brooklyn.shopme.common.entity.Customer;
import com.brooklyn.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	public boolean isEmailUnique(String email)
	{
		Customer customer = customerRepository.findByEmail(email);
		return customer == null;
	}
	public void registerCustomer(Customer customer){
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setVerificationCode(RandomString.make(64));
		customerRepository.save(customer);
	}
	private void encodePassword(Customer customer) {
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
	}
	public boolean verify(String verificationCode){
		Customer customer = customerRepository.findByVerificationCode(verificationCode);
		if(customer == null || customer.isEnabled()){
			return false;
		}
		else {
			customerRepository.enable(customer.getId());
		}
		return true;
	}
	public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType){
		if(!customer.getAuthenticationType().equals(authenticationType)){
			customerRepository.updateAutheticationType(customer.getId(), authenticationType);
		}
	}
	public Customer getCustomerByEmai(String email){
		return customerRepository.findByEmail(email);
	}
	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode ) {
		Customer customer = new Customer();
		customer.setEmail(email);
		setName(name, customer);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.GOOGLE);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepository.findByCode(countryCode));
		customerRepository.save(customer);
	}
	private void setName(String name, Customer customer) {
		String[] nameArray = name.split(" ");
		if (nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		} else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			
			String lastName = name.replaceFirst(firstName + " ", "");
			customer.setLastName(lastName);
		}
	}
}
