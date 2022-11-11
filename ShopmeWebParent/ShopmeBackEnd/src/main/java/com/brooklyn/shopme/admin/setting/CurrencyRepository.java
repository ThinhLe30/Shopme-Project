package com.brooklyn.shopme.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.brooklyn.shopme.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
	public List<Currency> findAllByOrderByNameAsc();
	
}
