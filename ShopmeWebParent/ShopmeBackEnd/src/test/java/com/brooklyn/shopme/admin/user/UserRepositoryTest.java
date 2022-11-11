package com.brooklyn.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.brooklyn.shopme.common.entity.Role;
import com.brooklyn.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	@Autowired
	UserRepository repository;
	@Autowired
	TestEntityManager testEntityManager;
	@Test
	public void testCreateNewUserWithOneRole()
	{
		Role roleAdmin =testEntityManager.find(Role.class, 1);
		User userBrooklyn = new User("brooklyn.site@gmail.com","123456","Brookly","Le");
		userBrooklyn.addRole(roleAdmin);
		User savedUser = repository.save(userBrooklyn);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	@Test
	public void testCreateNewUserWithTowRoles()
	{
		User userTokyo = new User("tokyo.site@gmail.com","123456","Tokyo","Tran");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		userTokyo.addRole(roleEditor);
		userTokyo.addRole(roleAssistant);
		
		User saveUser = repository.save(userTokyo);
		assertThat(saveUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUser()
	{
		Iterable<User> lisUsers = repository.findAll();
		lisUsers.forEach(user -> System.out.println(user));
	}
	@Test 
	public void getUserById()
	{
		User user = repository.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	@Test 
	public void testUpdateUserDetail()
	{
		User user = repository.findById(1).get();
		user.setEnable(true);
		user.setEmail("brooklynLee.site@gmail.com");
		repository.save(user);
		
	}
	@Test
	public void testUpdateUserRoles()
	{
		User userTokyo = repository.findById(2).get();
		Role role3 = new Role(3);
		userTokyo.getRoles().remove(role3);
		userTokyo.addRole(new Role(2));
		repository.save(userTokyo);
	}
	@Test
	public void deleteUserById()
	{
		repository.deleteById(2);
	}
	@Test
	public void testGetUserByEmail() {
		User user = repository.findByEmail("thinhmnsd2002@gmail.com");
		assertThat(user).isNotNull();
	}
	@Test
	public void testCountById()
	{
		Long idCountLong = repository.countById(3);
		assertThat(idCountLong).isNotNull().isGreaterThan(0);
	}
	@Test 
	public void testDisableUser()
	{
		repository.updateEnabledStatus(1, false);
	}
	@Test
	public void testListFirstPage(){
		int pageNumber =1;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repository.findAll(pageable);
		List<User> listUser = page.getContent();
		listUser.forEach(user -> System.out.println(user));
		assertThat(listUser.size()).isEqualTo(pageSize);
	}
	@Test
	public void testSearchUser(){
		String keyword ="Bruce";
		int pageNumber =0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repository.findAll(keyword,pageable);
		List<User> listUser = page.getContent();
		listUser.forEach(user -> System.out.println(user));
		assertThat(listUser.size()).isGreaterThan(0);
		
	}
}
