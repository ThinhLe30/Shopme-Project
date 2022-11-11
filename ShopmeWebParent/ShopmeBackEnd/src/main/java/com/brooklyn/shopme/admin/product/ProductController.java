package com.brooklyn.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brooklyn.shopme.admin.FileUploadUtil;
import com.brooklyn.shopme.admin.brand.BrandService;
import com.brooklyn.shopme.admin.category.CategoryService;
import com.brooklyn.shopme.admin.security.ShopmeUserDetails;
import com.brooklyn.shopme.common.entity.Brand;
import com.brooklyn.shopme.common.entity.Category;
import com.brooklyn.shopme.common.entity.Product;
import com.brooklyn.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	
	@GetMapping("/products")
	public String listFirstPage(Model theModel)
	{
		return listByPage(1, theModel, "name", "asc", null,0);
	}
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model theModel,
			@Param("sortField") String sortField, 
			@Param("orderBy") String orderBy,
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId
			)
	{
		if(sortField == null || sortField.isEmpty()) sortField ="name";
		if(orderBy == null || sortField.isEmpty()) orderBy = "asc";
		Page<Product> page = productService.listByPage(pageNum,sortField,orderBy,keyword, categoryId);
		List<Product> listProducts = page.getContent();
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		String reverseOrderBy = orderBy.equals("asc") ? "desc" : "asc";
		long startCount = (pageNum -1 ) * ProductService.PRODUCTS_PER_PAGE +1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE -1;
		if(endCount > page.getTotalElements())
		{
			endCount = page.getTotalElements();
		}
		if(categoryId != null){
			theModel.addAttribute("categoryId",categoryId);
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
		theModel.addAttribute("products", listProducts);
		theModel.addAttribute("categories", listCategories);
		
		return "products/products";
	}
	@GetMapping("/products/new")
	public String newProduct(Model theModel)
	{
		List<Brand> listBrands = brandService.listAll();
		Integer numberOfProductExtraImage = 0;
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		theModel.addAttribute("numberOfProductExtraImage",numberOfProductExtraImage);
		theModel.addAttribute("product",product);
		theModel.addAttribute("listBrands",listBrands);
		theModel.addAttribute("pageTitle","Create New Product");
		
		return "/products/product_form";
	}
	@PostMapping("/products/save")
	public String saveProduct(@ModelAttribute(name = "product") Product product,
			RedirectAttributes redirectAttributes,
			@RequestParam(value ="fileImage", required = false) MultipartFile multipartFile,
			@RequestParam(value="extraImage",required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs",required = false) String[] detailIDs,
			@RequestParam(name = "detailNames",required = false) String[] detailNames,
			@RequestParam(name = "detailValues",required = false) String[] detailValues,
			@RequestParam(name = "imageIDs",required = false) String[] imageIDs,
			@RequestParam(name = "imageNames",required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException
	{
			if(loggedUser.hasRole("Salesperson")){
				productService.saveProductPrice(product);
				redirectAttributes.addFlashAttribute("message", "The product have been saved successfully.");
				return "redirect:/products";
			}
			ProductSaveHelper.setMainImageName(multipartFile, product);
			ProductSaveHelper.setExistingExtraImageNames(imageIDs,imageNames,product);
			ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts,product);
			ProductSaveHelper.setProductDetails(detailIDs,detailNames,detailValues,product);
			
			Product saveProduct = productService.saveProduct(product);
			
			ProductSaveHelper.saveUploadedImages(multipartFile,extraImageMultiparts,saveProduct);
			ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);
			redirectAttributes.addFlashAttribute("message", "The product have been saved successfully.");
			
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id,Model theModel,
			RedirectAttributes redirectAttributes)
	{
		try {
			
			List<Brand> listBrands = brandService.listAll();
			Product product = productService.get(id);
			Integer numberOfProductExtraImage = product.getImages().size();
			theModel.addAttribute("product",product);
			theModel.addAttribute("numberOfProductExtraImage",numberOfProductExtraImage);
			theModel.addAttribute("pageTitle","Edit Product (ID: "+ id+")");
			theModel.addAttribute("listBrands",listBrands);
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
	}
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id,Model theModel,
			RedirectAttributes redirectAttributes)
	{
		try {
			Product product = productService.get(id);
			theModel.addAttribute("product",product);
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
	}


	@GetMapping("/products/{id}/enabled/{status}")
	public String updateUserStatus(@PathVariable(name = "id") Integer id,
							@PathVariable(name = "status") boolean enabled,
							RedirectAttributes redirectAttributes) throws ProductNotFoundException {
		productService.updateProductStatus(id, enabled);
		String status = enabled? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been " + status+".");
		
		return "redirect:/products";
	}
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id,
			RedirectAttributes redirectAttributes){
		
		try {
			productService.deleteProduct(id);
			String productExtraImgDir = "../product-images/" + id +"/extras";
			String productMainImgDir = "../product-images/" + id ;
			FileUploadUtil.removeDir(productExtraImgDir);
			FileUploadUtil.removeDir(productMainImgDir);
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted.");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}
	
}
