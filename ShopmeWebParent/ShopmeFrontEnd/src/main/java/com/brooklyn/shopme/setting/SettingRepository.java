package com.brooklyn.shopme.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.brooklyn.shopme.common.entity.Setting;
import com.brooklyn.shopme.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {
	public List<Setting> findByCategory(SettingCategory category);
	
	@Query("select s from Setting s where s.category = ?1 or s.category = ?2")
	public List<Setting> findBy2Categories(SettingCategory catOne, SettingCategory catTwo);
}
