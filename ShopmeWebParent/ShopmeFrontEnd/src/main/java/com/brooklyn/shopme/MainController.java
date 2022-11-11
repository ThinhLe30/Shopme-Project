package com.brooklyn.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.brooklyn.shopme.category.CategoryService;
import com.brooklyn.shopme.common.entity.Category;

@Controller
public class MainController {
	@Autowired
	private CategoryService categoryService;
	@GetMapping("")
	public String viewHomePage(Model theModel)
	{
		List<Category> categories = categoryService.listNoChildrenCategories();
		theModel.addAttribute("categories", categories);
		return "index";
	}
	@GetMapping("/login")
	public String viewLoginPage(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
		{
			return "login";			
		}
		return "redirect:/";
	}
}
