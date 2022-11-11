package com.brooklyn.shopme.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brooklyn.shopme.admin.FileUploadUtil;
import com.brooklyn.shopme.admin.security.ShopmeUserDetails;
import com.brooklyn.shopme.admin.user.UserService;
import com.brooklyn.shopme.common.entity.User;

@Controller
public class AccountController {
	@Autowired
	private UserService userService;
	@GetMapping("/account")
	public String viewDetailAccount(@AuthenticationPrincipal ShopmeUserDetails loogedUser,
			Model theModel){
		String email = loogedUser.getUsername();
		User user = userService.findByEmail(email);
		theModel.addAttribute("user", user);
		return "users/account_form";
	}
	@PostMapping("/account/update")
	public String saveDetails(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShopmeUserDetails loogedUser,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty())
		{
			// the form had upload file
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User userSaved = userService.updateAccount(user);
			String uploadDir = "user-photo/" + userSaved.getId();
			FileUploadUtil.clearDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else{
			if(user.getPhotos().isEmpty()){
				user.setPhotos(null);
				userService.updateAccount(user);
			}
			else{
				userService.updateAccount(user);
			}
		}
		loogedUser.setFirstname(user.getFirstName());
		loogedUser.setLastname(user.getLastName());
		redirectAttributes.addFlashAttribute("message", "Your account detail have been updated.");
		return "redirect:/account";
	}
}
