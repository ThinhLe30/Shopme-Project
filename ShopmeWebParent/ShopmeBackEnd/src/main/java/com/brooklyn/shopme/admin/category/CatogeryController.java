package com.brooklyn.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.brooklyn.shopme.common.entity.Category;
import com.brooklyn.shopme.common.exception.CategoryNotFoundException;

@Controller
public class CatogeryController {
	@Autowired
	private CategoryService categoryService;
	@GetMapping("/categories")
	public String listFirstPage(Model theModel,@Param("orderBy") String orderBy)
	{
		return listByPage(1, theModel, orderBy,null);
	}
	@GetMapping("categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum,
			Model theModel,@Param("orderBy") String orderBy,@Param("keyword") String keyword){
		
		if(orderBy == null || orderBy.isEmpty()){
			 orderBy = "asc";
		}
		CategoryPageInfo pageInfo =  new CategoryPageInfo();
		List<Category> listCategories = categoryService.listByPage(pageInfo,pageNum,orderBy,keyword);
		String reverseOrderBy = orderBy.equals("asc") ? "desc" : "asc"; 
		long startCount = (pageNum -1 ) * CategoryService.ROOT_CATEGORY_PER_PAGE +1;
		long endCount = startCount + CategoryService.ROOT_CATEGORY_PER_PAGE -1;
		if(endCount > pageInfo.getTotalElement())
		{
			endCount = pageInfo.getTotalElement();
		}  
		theModel.addAttribute("startCount",startCount);
		theModel.addAttribute("endCount",endCount);
		theModel.addAttribute("totalPage",pageInfo.getTotalPages());
		theModel.addAttribute("totalItems",pageInfo.getTotalElement());
		theModel.addAttribute("currentPage",pageNum);		
		theModel.addAttribute("categories",listCategories);
		theModel.addAttribute("keyword",keyword);
		theModel.addAttribute("sortField","name");
		theModel.addAttribute("orderBy",orderBy	);
		
		theModel.addAttribute("reverseOrderBy",reverseOrderBy);
		return "categories/categories";
	}
	@GetMapping("/categories/new")
	public String newCategory(Model theModel)
	{
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		theModel.addAttribute("category",new Category());
		theModel.addAttribute("listCategories",listCategories);
		theModel.addAttribute("pageTitle","Create New Category");
		
		return "categories/category_form";
	}
	@PostMapping("/categories/save")
	public String saveCategories(@ModelAttribute("category") Category cate,
			RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException{
		Category savedCategory;
		if(!multipartFile.isEmpty())
		{
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			cate.setImage(fileName);
			savedCategory = categoryService.save(cate);
			String uploadDir = "../category-images/" + savedCategory.getId();
			FileUploadUtil.clearDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else{
			savedCategory =categoryService.save(cate);
		}
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}
	@GetMapping("/categories/edit/{id}")
	public String updateCategory(@PathVariable(name = "id") Integer id,
			Model theModel, RedirectAttributes redirectAttributes){
				
			try {
				Category category = categoryService.get(id);
				List<Category> listCategories = categoryService.listCategoriesUsedInForm();
				theModel.addAttribute("category",category);
				theModel.addAttribute("listCategories",listCategories);
				theModel.addAttribute("pageTitle","Edit Category (ID: "+ id +")");
				return "categories/category_form";
			} catch (CategoryNotFoundException e) {
				redirectAttributes.addFlashAttribute("message", e.getMessage());
				return "redirect:/categories/";
			}
	}
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCateStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean status,
			RedirectAttributes redirectAttributes)
	
	{
		categoryService.updateCategoryEnabledStatus(id, status);
		String sta = status ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The category ID " + id+ " has been "+sta);
		return "redirect:/categories";	
	}
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes)
	{
		try {
			categoryService.delete(id);
			String cateDir ="../category-images/" + id;
			FileUploadUtil.removeDir(cateDir);
			redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted successfully");

		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/categories";	
	}
}
