package com.brooklyn.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brooklyn.shopme.common.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer>{
	public User findByEmail(String email);
	public Long countById(Integer id);
	@Query("update User u set u.enable = ?2 where u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id,boolean enabled);
	
	@Query("select u from User u where concat(u.id, ' ', u.firstName, ' ', u.lastName, ' ', u.email) like %?1%")
	public Page<User> findAll(String keyword,Pageable pageable);
}
