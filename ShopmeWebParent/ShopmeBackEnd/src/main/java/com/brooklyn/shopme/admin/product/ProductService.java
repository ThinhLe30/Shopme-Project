package com.brooklyn.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.brooklyn.shopme.common.entity.Product;
import com.brooklyn.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	@Autowired
	private ProductRepository productRepository;
	public static final int PRODUCTS_PER_PAGE = 5;
	public List<Product> listAll()
	{
		return (List<Product>) productRepository.findAll();
	}
	public Page<Product> listByPage(int pageNum, String sortField, String orderBy,String keyword, Integer categoryId){
		
		Sort sort = Sort.by(sortField);
		sort = orderBy.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE,sort);
		if(keyword != null && !keyword.isEmpty())
		{
			// in case that the keyword is not null, but we also selected the category, so we search all by category and keyword
			if(categoryId != null && categoryId > 0){
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return productRepository.searchByCategoryWithKeyword(categoryId, categoryIdMatch,keyword, pageable);
			}
			// the keyword is not null and search for all category
			return productRepository.findAll(keyword,pageable);
		}
		// in case that the keyword is null , we search for only category
		if(categoryId != null && categoryId > 0){
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return productRepository.findAllByCategory(categoryId, categoryIdMatch, pageable);
		}
		return productRepository.findAll(pageable);
	}
	public Product saveProduct(Product product)
	{
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		if(product.getAlias() == null || product.getAlias().isEmpty())
		{
			product.setAlias(product.getName().replaceAll(" ","_"));
		}else{
			product.setAlias(product.getAlias().replaceAll(" ", "_"));
		}
		product.setUpdatedTime(new Date());
		return productRepository.save(product);
	}
	public void saveProductPrice(Product productInForm){
		Product productInDB = productRepository.findById(productInForm.getId()).get();
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setCost(productInForm.getCost());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());
		productRepository.save(productInDB);
	}
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = productRepository.findByName(name);
		
		if (isCreatingNew) {
			if (productByName != null) return "Duplicate";
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	public void updateProductStatus(Integer id, boolean status)
	{
		productRepository.updateEnabledStatus(id, status);
	}
	public void deleteProduct(Integer id) throws ProductNotFoundException
	{
		Long countById = productRepository.countById(id);
		if(countById == null || countById == 0){
			throw new ProductNotFoundException("Could not find any product with ID: "+id);
		}
		productRepository.deleteById(id);
	}
	public Product get(Integer id) throws ProductNotFoundException{
		try {
			return productRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find product with ID: "+id);
		}
	}
}
