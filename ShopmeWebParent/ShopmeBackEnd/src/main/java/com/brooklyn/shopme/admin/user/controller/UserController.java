package com.brooklyn.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brooklyn.shopme.admin.FileUploadUtil;
import com.brooklyn.shopme.admin.user.UserNotFoundException;
import com.brooklyn.shopme.admin.user.UserService;
import com.brooklyn.shopme.common.entity.Role;
import com.brooklyn.shopme.common.entity.User;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public String listFirstPage(Model theModel) {

		return listByPage(1, theModel,"firstName","asc",null);
	}
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model theModel,
			@Param("sortField") String sortField, 
			@Param("orderBy") String orderBy,
			@Param("keyword") String keyword
			)
	{
		Page<User> page = userService.listByPage(pageNum,sortField,orderBy,keyword);
		List<User> listUsers = page.getContent();
		String reverseOrderBy = orderBy.equals("asc") ? "desc" : "asc";
		long startCount = (pageNum -1 ) * UserService.USERS_PER_PAGE +1;
		long endCount = startCount + UserService.USERS_PER_PAGE -1;
		if(endCount > page.getTotalElements())
		{
			endCount = page.getTotalElements();
		}
		theModel.addAttribute("totalItems",page.getTotalElements());
		theModel.addAttribute("startCount",startCount);
		theModel.addAttribute("endCount",endCount);
		theModel.addAttribute("currentPage",pageNum);
		theModel.addAttribute("totalPage",page.getTotalPages());
		theModel.addAttribute("sortField",sortField);
		theModel.addAttribute("orderBy",orderBy);
		theModel.addAttribute("keyword",keyword);
		theModel.addAttribute("reverseOrderBy",reverseOrderBy);
		theModel.addAttribute("users", listUsers);
		
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model theModel) {
		List<Role> listRoles = userService.listRole();
		User user = new User();
		user.setEnable(true);
		theModel.addAttribute("user", user);
		theModel.addAttribute("listRoles", listRoles);
		theModel.addAttribute("pageTitle","Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if(!multipartFile.isEmpty())
		{
			// the form had upload file
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User userSaved = userService.save(user);
			String uploadDir = "user-photo/" + userSaved.getId();
			FileUploadUtil.clearDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else{
			if(user.getPhotos().isEmpty()){
				user.setPhotos(null);
				userService.save(user);
			}
			else{
				userService.save(user);
			}
		}
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
		return "redirect:/users/page/1?sortField=id&orderBy=asc&keyword=" +user.getEmail().split("@")[0];
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model theModel,
			RedirectAttributes redirectAttributes) {
		try {
			List<Role> listRoles = userService.listRole();
			User user = userService.get(id);
			theModel.addAttribute("user", user);
			theModel.addAttribute("pageTitle","Edit User (ID: "+id+")");
			theModel.addAttribute("listRoles", listRoles);
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}
	}
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model theModel,
			RedirectAttributes redirectAttributes)
	{
		try {
			userService.delete(id);
			String cateDir ="/user-photo/" + id;
			FileUploadUtil.removeDir(cateDir);
			redirectAttributes.addFlashAttribute("message", "The user ID" + id + " has been deleted successfully");

		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
		
	}
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserStatus(@PathVariable(name = "id") Integer id,
							@PathVariable(name = "status") boolean enabled,
							RedirectAttributes redirectAttributes) throws UserNotFoundException {
		userService.updateUserStatus(id, enabled);
		String status = enabled? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been " + status );
		return "redirect:/users/page/1?sortField=id&orderBy=asc&keyword=" +userService.get(id).getEmail().split("@")[0];
	}
}
