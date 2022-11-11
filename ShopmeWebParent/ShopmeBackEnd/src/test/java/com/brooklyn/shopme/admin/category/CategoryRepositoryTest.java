package com.brooklyn.shopme.admin.category;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.brooklyn.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository repository;
	@Test
	public void testCreateRootCategory(){
		Category category = new Category("Electronics");
		Category savedCategory = repository.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	@Test
	public void testCreateSubCategory(){
		Category parent = new Category(7);
		Category category1 = new Category("iPhone",parent);
		repository.save(category1);
	}
	@Test 
	public void testGetCategory(){
		Category category = repository.findById(2).get();
		System.out.println(category.getName());
		Set<Category> children = category.getChildren();
		for (Category subCategory : children)
		{
			System.out.println(subCategory.getName());
		}
		assertThat(children.size()).isGreaterThan(0);
	}
	@Test
	public void testPrintCategoryByLevel(){
		 Iterable<Category> categories = repository.findAll();
		 for(Category category : categories)
		 {
			 if(category.getParent() == null){
				 System.out.println(category.getName());
				 Set<Category> children = category.getChildren();
				 for(Category subCategory : children){
					System.out.println("	--"+subCategory.getName());
					 Set<Category> children2 = subCategory.getChildren();
					 for(Category subCategory2 : children2){
						 System.out.println("		--"+subCategory2.getName());
					 }
				 }
			 }
		 }
	}
}	
