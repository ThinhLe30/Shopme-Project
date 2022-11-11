package com.brooklyn.shopme.admin.setting;

import java.util.List;

import com.brooklyn.shopme.common.entity.Setting;
import com.brooklyn.shopme.common.entity.SettingBag;

public class GeneralSettingBag extends SettingBag{

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}
	public void updateCurrencySymbol(String value){
		super.update("CURRENCY_SYMBOL", value);
	}
	public void updateSiteLogo(String value){
		super.update("SITE_LOGO", value);
	}
	
	
}
