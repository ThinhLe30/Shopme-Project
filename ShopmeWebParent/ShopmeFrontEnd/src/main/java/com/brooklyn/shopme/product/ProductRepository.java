package com.brooklyn.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brooklyn.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
	@Query("select p from Product p where p.enabled = true "
			+ "and (p.category.id = ?1 or p.category.allParentIDs like %?2%) "
			+ "order by p.name asc")
	public Page<Product> listByCategory(Integer cateId, String categoryIDMatch,Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products WHERE enabled = true AND "
			+ "MATCH(name, short_description, full_description) AGAINST (?1)", 
			nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
}
