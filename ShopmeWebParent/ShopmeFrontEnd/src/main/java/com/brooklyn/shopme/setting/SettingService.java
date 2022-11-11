package com.brooklyn.shopme.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brooklyn.shopme.common.entity.Setting;
import com.brooklyn.shopme.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository settingRepository;
	

	public List<Setting> getGeneralSettings(){
		return settingRepository.findBy2Categories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	public EmailSettingBag getEmailSettings(){
		List<Setting> listSettings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
		listSettings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(listSettings);
	}
}
