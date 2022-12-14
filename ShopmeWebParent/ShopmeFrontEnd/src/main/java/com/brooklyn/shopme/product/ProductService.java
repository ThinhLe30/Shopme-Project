package com.brooklyn.shopme.product;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.brooklyn.shopme.common.entity.Product;
import com.brooklyn.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;
	@Autowired
	private ProductRepository productRepository;
	
	public Page<Product> listByCategory(Integer pageNum, Integer categoryID){
		String cateIDMath = "-"+ String.valueOf(categoryID)+"-";
		Pageable pageable = PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE);
		return productRepository.listByCategory(categoryID, cateIDMath, pageable);
	}
	
	public Product getProductByAlias(String alias) throws ProductNotFoundException{
		 Product product = productRepository.findByAlias(alias);
		 if(product == null){
			 throw new ProductNotFoundException("Could not find any product with alias: "+alias);
		 }
		 return product;
	}
	
	public Product getProduct(Integer id) throws ProductNotFoundException {
		try {
			Product product = productRepository.findById(id).get();
			return product;
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}	
	
	public Page<Product> search(String keyword, int pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return productRepository.search(keyword, pageable);
		
	}

}
