package com.brooklyn.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.brooklyn.shopme.common.entity.Role;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void testCreateFirstRole()
	{
		Role adminRole = new Role("Admin","manage everything");
		Role saveRole = roleRepository.save(adminRole);
		assertThat(saveRole.getId()).isGreaterThan(0);
	}
	@Test
	public void testCreateRestRole()
	{
		Role SalesPersonRole = new Role("Salesperson","manage product price, customers, shipping,"
				+ "orders and sales report");
		Role EditorRole = new Role("Editor","manage categories, brands, products, articles and menus");
		Role ShipperRole = new Role("Shipper","view products, view orders and update order status");
		Role AssistantRole = new Role("Assistant","manage questions and reviews");
		
		roleRepository.saveAll(List.of(SalesPersonRole,EditorRole,ShipperRole,AssistantRole));
	}

}
