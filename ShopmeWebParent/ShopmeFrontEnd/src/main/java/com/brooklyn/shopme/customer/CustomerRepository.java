package com.brooklyn.shopme.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.brooklyn.shopme.common.entity.AuthenticationType;
import com.brooklyn.shopme.common.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, java.lang.Integer> {
	@Query("select c from Customer c where c.email = ?1")
	public Customer findByEmail(String email);
	
	@Query("select c from Customer c where c.verificationCode = ?1")
	public Customer findByVerificationCode(String code);
	
	@Query("update Customer c set c.enabled = true ,c.verificationCode = null where c.id = ?1")
	@Modifying	
	public void enable(Integer id);
	@Query("update Customer c set c.authenticationType = ?2 where c.id = ?1")
	@Modifying
	public void updateAutheticationType(Integer id ,AuthenticationType type);
}
