package com.brooklyn.shopme.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.brooklyn.shopme.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {
	public List<Country> findAllByOrderByNameAsc();
	
	@Query("select c from Country c where c.code = ?1")
	public Country findByCode(String code);
}
