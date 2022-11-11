package com.brooklyn.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.brooklyn.shopme.category.CategoryService;
import com.brooklyn.shopme.common.entity.Category;
import com.brooklyn.shopme.common.entity.Product;
import com.brooklyn.shopme.common.exception.CategoryNotFoundException;
import com.brooklyn.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable(name = "category_alias") String alias,
			Model theModel){
			return viewCategoryByPage(alias, theModel, 1);
	}
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable(name = "category_alias") String alias,
			Model theModel,
			@PathVariable("pageNum") int pageNum){
		try {
			Category category = categoryService.getCategoryByAlias(alias);
			List<Category> listCategoryParent = categoryService.getCategoryParents(category);
			Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
			List<Product> listProducts = pageProducts.getContent();
			long startCount = (pageNum -1 ) * ProductService.PRODUCTS_PER_PAGE +1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE -1;
			if(endCount > pageProducts.getTotalElements())
			{
				endCount = pageProducts.getTotalElements();
			}
			theModel.addAttribute("totalItems",pageProducts.getTotalElements());
			theModel.addAttribute("startCount",startCount);
			theModel.addAttribute("endCount",endCount);
			theModel.addAttribute("currentPage",pageNum);
			theModel.addAttribute("totalPage",pageProducts.getTotalPages());
			theModel.addAttribute("pageTitle",category.getName());
			theModel.addAttribute("listCategoryParents",listCategoryParent);
			theModel.addAttribute("listProducts",listProducts);
			theModel.addAttribute("category",category);
			return "/products/products_by_category";
		} 
		catch (CategoryNotFoundException e) {
			return "error/404";
		}
		
	}
	@GetMapping("/p/{product_alias}")
	public String viewDetailsProduct(@PathVariable(name = "product_alias") String alias,
			Model theModel){
		try {
			Product product = productService.getProductByAlias(alias);
			List<Category> listCategoryParent = categoryService.getCategoryParents(product.getCategory());
			
			theModel.addAttribute("pageTitle",product.getShortName());
			theModel.addAttribute("product",product);
			theModel.addAttribute("listCategoryParents",listCategoryParent);
			return "/products/products_detail";
		} catch (ProductNotFoundException e) {
			return "/error/404";
		}
	}
	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword, Model theModel){
		return searchByPage(1, keyword, theModel);
	}
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@PathVariable(name = "pageNum") int pageNum,
			@Param("keyword") String keyword, Model theModel){
		Page<Product> pageProducts = productService.search(keyword,pageNum);
		List<Product> listProducts = pageProducts.getContent();	
		long startCount = (pageNum -1 ) * ProductService.SEARCH_RESULTS_PER_PAGE +1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE -1;
		if(endCount > pageProducts.getTotalElements())
		{
			endCount = pageProducts.getTotalElements();
		}
		theModel.addAttribute("totalItems",pageProducts.getTotalElements());
		theModel.addAttribute("startCount",startCount);
		theModel.addAttribute("endCount",endCount);
		theModel.addAttribute("currentPage",pageNum);
		theModel.addAttribute("totalPage",pageProducts.getTotalPages());
		theModel.addAttribute("pageTitle",keyword + "- Search Result");
		theModel.addAttribute("keyword",keyword);		
		theModel.addAttribute("listResults",listProducts);
		return "products/search_result";
	}
}
