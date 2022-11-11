package com.brooklyn.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brooklyn.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	@Query("select c from Category c where c.parent.id is null")
	public List<Category> listRootCategory(Sort sort);
	
	@Query("select c from Category c where c.parent.id is null")
	public Page<Category> listRootCategory(Pageable pageable);
	
	@Query("select c from Category c where c.name like %?1%")
	public Page<Category> search(String keyword,Pageable pageable);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	@Query("update Category u set u.enabled = ?2 where u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id,boolean enabled);

	public Long countById(Integer id);
}
