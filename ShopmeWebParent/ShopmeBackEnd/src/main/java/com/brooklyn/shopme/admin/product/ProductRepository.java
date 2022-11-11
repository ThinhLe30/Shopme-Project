package com.brooklyn.shopme.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brooklyn.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
	public Product findByName(String name);
	
	@Query("update Product p set p.enabled = ?2 where p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id,boolean enabled);
	public Long countById(Integer id);
	@Query("select p from Product p where p.name like %?1%"
	+ " or p.shortDescription like %?1%"
	+ " or p.fullDescription like %?1%"
	+ " or p.brand.name like %?1%"
	+ " or p.category.name like %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable); 
	
	@Query("select p from Product p where p.category.id = ?1 "
			+ "or p.category.allParentIDs like %?2%")
	public Page<Product> findAllByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);
	@Query("select p from Product p where (p.category.id = ?1 "
			+ "or p.category.allParentIDs like %?2%)"
			+ "and"
			+ " (p.name like %?3%"
			+ " or p.shortDescription like %?3%"
			+ " or p.fullDescription like %?3%"
			+ " or p.brand.name like %?3%"
			+ " or p.category.name like %?3%)")
	public Page<Product> searchByCategoryWithKeyword(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable) ;
}
