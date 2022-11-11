package com.brooklyn.shopme.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.brooklyn.shopme.common.entity.Setting;
import com.brooklyn.shopme.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {
	public List<Setting> findByCategory(SettingCategory category);
}
