package com.brooklyn.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.brooklyn.shopme.admin.category.CategoryService;
import com.brooklyn.shopme.common.entity.Brand;
import com.brooklyn.shopme.common.entity.Category;

@Controller
public class BrandController {
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping("/brands")
	public String listAll (Model theModel){
		List<Brand> listBrands = brandService.listAll();
		theModel.addAttribute("brands", listBrands);
		return "/brands/brands";
	}
	@GetMapping("/brands/new")
	public String newBrand(Model theModel)
	{
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		theModel.addAttribute("listCategories", listCategories);
		theModel.addAttribute("brand", new Brand());
		theModel.addAttribute("pageTitle", "Ceate New Brand");
		return "/brands/brand_form";
	}
	@PostMapping("/brands/save")
	public String saveBrands(@ModelAttribute("brand") Brand brand,
			RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException{
		Brand savedBrand;
		if(!multipartFile.isEmpty())
		{
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			savedBrand = brandService.save(brand);
			String uploadDir = "../brand-logos/" + savedBrand.getId();
			FileUploadUtil.clearDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		else{
			savedBrand = brandService.save(brand);
		}
		redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
		return "redirect:/brands";
	}
	@GetMapping("/brands/edit/{id}")
	public String updateCategory(@PathVariable(name = "id") Integer id,
			Model theModel, RedirectAttributes redirectAttributes){
				
			try {
				Brand brand = brandService.get(id);
				List<Category> listCategories = categoryService.listCategoriesUsedInForm();
				theModel.addAttribute("brand",brand);
				theModel.addAttribute("listCategories",listCategories);
				theModel.addAttribute("pageTitle","Edit Brand (ID: "+ id +")");
				return "brands/brand_form";
			} catch (BrandNotFoundException e) {
				redirectAttributes.addFlashAttribute("message", e.getMessage());
				return "redirect:/brands/";
			}
	}
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes)
	{
		try {	
			brandService.delete(id);
			String cateDir ="../brand-logos/" + id;
			FileUploadUtil.removeDir(cateDir);
			redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully");

		} catch (BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/brands";	
	}
}
