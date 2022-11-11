package com.brooklyn.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.access.method.P;

import com.brooklyn.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void testListCategoryEnabled(){
		List<Category> listCategoriesEnabled = categoryRepository.findAllEnabled();
		listCategoriesEnabled.forEach(category -> {
			System.out.println(category.getName() + "( " + category.isEnabled() +" )");
		});
	}
	@Test 
	public void testFindCategoryByAlias(){
		String alias = "cellphone_cables_adapters";
		Category category = categoryRepository.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
	}
}
