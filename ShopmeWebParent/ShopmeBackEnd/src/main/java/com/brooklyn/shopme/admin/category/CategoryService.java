package com.brooklyn.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.brooklyn.shopme.common.entity.Category;
import com.brooklyn.shopme.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {
	public static final int ROOT_CATEGORY_PER_PAGE = 4;
	@Autowired
	private CategoryRepository categoryRepository;
	public List<Category> listByPage(CategoryPageInfo pageInfo,int pageNUm,String orderBy,String keyword){
		Sort sort = Sort.by("name");
		
		if (orderBy.equals("asc")){
			 sort =sort.ascending();
		}
		
		else if(orderBy.equals("desc")){
			 sort =sort.descending();
		}

		Pageable pageable = PageRequest.of(pageNUm-1,ROOT_CATEGORY_PER_PAGE,sort);
		Page<Category> pageCategories = null;
		if(keyword != null  && !keyword.isEmpty())
		{
			pageCategories = categoryRepository.search(keyword,pageable);
		}
		else{
			pageCategories = categoryRepository.listRootCategory(pageable);
		}
		
		List<Category> rootCategories = pageCategories.getContent();
		pageInfo.setTotalElement(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		if(keyword != null  && !keyword.isEmpty())
		{
			List<Category> searchResults = pageCategories.getContent();
			for(Category category : searchResults)
			{
				category.setHasChildern(category.getChildren().size() > 0);
			}
			return searchResults;
		}
		else{
			return listHierarchicalCategories(rootCategories,orderBy);
		}
		
	}
	private List<Category> listHierarchicalCategories(List<Category> rootCategories,String orderBy){
		List<Category> listHierarchicalCategories = new ArrayList<>();
		for(Category rootCategory : rootCategories){
			listHierarchicalCategories.add(Category.copyFull(rootCategory));
			Set<Category> children = sortSubCategories(rootCategory.getChildren(),orderBy);
			for(Category subCategory : children)
			{
				String name = "--" + subCategory.getName();
				listHierarchicalCategories.add(Category.copyFull(subCategory, name));
				listHierarchicalSubCategories(subCategory,1,listHierarchicalCategories,orderBy);
			}
		}
		
		return listHierarchicalCategories;
	}
	private void listHierarchicalSubCategories(Category parent, int level, List<Category> listHierarchicalCategories,String orderBy){
		Set<Category> children = sortSubCategories(parent.getChildren(),orderBy);
		int newSublevel = level+1;
		for(Category subCategory : children)
		{
			String name = "";
			for(int i=0;i<newSublevel;i++){
				name += "--";
			}
			name += subCategory.getName();
			listHierarchicalCategories.add(Category.copyFull(subCategory, name));
			listHierarchicalSubCategories(subCategory,newSublevel,listHierarchicalCategories,orderBy);
		}
	}
	public Category save(Category category){
		Category parent =category.getParent();
		if(parent != null){
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		return categoryRepository.save(category);
	}
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesReturn = new ArrayList<>();
		Iterable<Category> categoriesInDB = categoryRepository.listRootCategory(Sort.by("name").ascending());
		for(Category category : categoriesInDB)
		{
			if(category.getParent() == null){
				categoriesReturn.add(new Category(category.getId(),category.getName()));
				Set<Category> children =sortSubCategories(category.getChildren());
				for(Category subCategory : children){
					categoriesReturn.add(new Category(subCategory.getId(),"--"+subCategory.getName()));
					listSubCategoriesUsedInForm(subCategory,1,categoriesReturn);
				}
			}
		}
		return categoriesReturn;
	}
	private void listSubCategoriesUsedInForm(Category parent, int subLevel, List<Category> categoriesReturn) {
		int newSublevel = subLevel+1;
		Set<Category> children = sortSubCategories(parent.getChildren());
		
		for(Category subCategory : children){
			String name = "";
			for(int i=0;i<newSublevel;i++){
				name += "--";
			}
			name += subCategory.getName();
			categoriesReturn.add(new Category(subCategory.getId(),name));
			listSubCategoriesUsedInForm(subCategory, newSublevel,categoriesReturn);
		}
	}
	public Category get(Integer id) throws CategoryNotFoundException
	{
		try {
			return categoryRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any category with ID: "+id);
		}
	}
	public String checkUnique(Integer id, String name, String alias){
		boolean isCreatingNew = (id == null || id == 0);
		Category categoryByName = categoryRepository.findByName(name);
		if(isCreatingNew)
		{
			if(categoryByName != null){
				return "DuplicatedName";
			}
			else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if(categoryByAlias != null){
					return "DuplicatedAlias";
				}
			}
		}
		else {
			{
				if(categoryByName != null && categoryByName.getId() != id)
				{
					return "DuplicatedName";
				}
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if(categoryByAlias != null && categoryByAlias.getId() != id)
				{
					return "DuplicatedAlias";
				}
			}
		}
		return "OK";
	}
	private SortedSet<Category> sortSubCategories(Set<Category> children){
		return sortSubCategories(children,"asc");
	}
	private SortedSet<Category> sortSubCategories(Set<Category> children,String orderBy)
	{
		SortedSet<Category> sortedChildern = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category o1, Category o2) {
				if(orderBy.equals("asc")){
					return o1.getName().compareTo(o2.getName());
				}
				else{
					return o2.getName().compareTo(o1.getName());
				}
			}
		});
		sortedChildern.addAll(children);
		return sortedChildern;
	}
	public void updateCategoryEnabledStatus(Integer id, boolean status){
		categoryRepository.updateEnabledStatus(id, status);
	}
	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = categoryRepository.countById(id);
		if(countById == null || countById == 0)
		{
			throw new CategoryNotFoundException("Could not find any category with ID "+ id);
		}
		categoryRepository.deleteById(id);
	}
}
