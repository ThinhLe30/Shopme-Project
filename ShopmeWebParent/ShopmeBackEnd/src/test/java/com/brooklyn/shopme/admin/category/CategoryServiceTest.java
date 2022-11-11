package com.brooklyn.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.brooklyn.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

		@MockBean
		private CategoryRepository repository;
		@InjectMocks
		private CategoryService service;
		
		@Test
		public void testCheckUniqueName()
		{
			Integer id = 1;
			String name = "Computers";
			String alias ="abc";
			Category cate = new Category(id,name,alias);
			Mockito.when(repository.findByName(name)).thenReturn(cate);
			Mockito.when(repository.findByAlias(alias)).thenReturn(cate);
			String result = service.checkUnique(id, name, alias);
			assertThat(result).isEqualTo("DuplicatedAlias");
		}
}
