package com.brooklyn.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brooklyn.shopme.admin.FileUploadUtil;
import com.brooklyn.shopme.common.entity.Currency;
import com.brooklyn.shopme.common.entity.Setting;


@Controller
public class SettingController {
	@Autowired
	private SettingService settingService;
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@GetMapping("/settings")
	public String listAll(Model theModel){
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

		theModel.addAttribute("listCurrencies",listCurrencies);
		
		for(Setting setting: listSettings)
		{
			theModel.addAttribute(setting.getKey(),setting.getValue());
		}
		return "settings/settings";
	}
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException{
		GeneralSettingBag settingBag = settingService.getGeneralSettings();
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request,settingBag);
		updateSettingsValueFromForm(request,settingBag.list());
		redirectAttributes.addFlashAttribute("message", "General settings have been saved.");
		return "redirect:/settings";
	}
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException{
		List<Setting> mailServerSettings = settingService.getMailServerSettings();
		updateSettingsValueFromForm(request, mailServerSettings);
		redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved.");
		return "redirect:/settings";
	}
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException{
		List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
		updateSettingsValueFromForm(request, mailTemplateSettings);
		redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved.");
		return "redirect:/settings";
	}
	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if(!multipartFile.isEmpty()){
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
			String uploadDir = "../site-logo/";
			FileUploadUtil.clearDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}
	private void saveCurrencySymbol(HttpServletRequest request,GeneralSettingBag settingBag){
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findById = currencyRepository.findById(currencyId);
		if(findById.isPresent()){
			Currency currency = findById.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}
	private void updateSettingsValueFromForm(HttpServletRequest request,List<Setting> listSettings){
		for(Setting setting : listSettings){
			String value = request.getParameter(setting.getKey());
			if(value != null){
				setting.setValue(value);
			}
		}
		settingService.saveAll(listSettings);
	}
}
