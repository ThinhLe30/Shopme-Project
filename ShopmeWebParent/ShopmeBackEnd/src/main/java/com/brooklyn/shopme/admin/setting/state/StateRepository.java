package com.brooklyn.shopme.admin.setting.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.brooklyn.shopme.common.entity.Country;
import com.brooklyn.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	public List<State> findByCountryOrderByNameAsc(Country country);
}
