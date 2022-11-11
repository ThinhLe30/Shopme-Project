package com.brooklyn.shopme.admin.brand;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.brooklyn.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
	public Long countById(Integer id);
	
	public Brand findByName(String name);
	
	@Query("select new Brand(b.id,b.name) from Brand b order by b.name asc")
	public List<Brand> findAll();
	
	
}
