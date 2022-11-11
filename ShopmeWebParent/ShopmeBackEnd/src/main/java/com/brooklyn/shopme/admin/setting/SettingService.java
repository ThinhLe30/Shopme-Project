package com.brooklyn.shopme.admin.setting;

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
	
	public List<Setting> listAllSettings(){
		return (List<Setting>) settingRepository.findAll();
	}
	public GeneralSettingBag getGeneralSettings(){
		List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
		List<Setting> settings = new ArrayList<>();
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		return new GeneralSettingBag(settings);
	}
	public void saveAll(Iterable<Setting> settings){
		settingRepository.saveAll(settings);
	}
	public List<Setting> getMailServerSettings(){
		return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
	}
	public List<Setting> getMailTemplateSettings(){
		return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
}
