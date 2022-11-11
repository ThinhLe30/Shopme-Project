package com.brooklyn.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brooklyn.shopme.common.entity.Category;
import com.brooklyn.shopme.common.exception.CategoryNotFoundException;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> listNoChildrenCategories(){
		List<Category> listNoChildrenCategory = new ArrayList<>();
		List<Category> listEnabledCategory = categoryRepository.findAllEnabled();
		listEnabledCategory.forEach(category -> {
			Set<Category> children = category.getChildren();
			if(children == null || children.size() == 0){
				listNoChildrenCategory.add(category);
			}
		});
		return listNoChildrenCategory;
	}
	public Category getCategoryByAlias(String alias) throws CategoryNotFoundException{
		Category category = categoryRepository.findByAliasEnabled(alias);
		if(category == null)
		{
			throw new CategoryNotFoundException("Cound not find any category with alias: "+alias);
		}
		
		return category;
	}
	public List<Category> getCategoryParents(Category child){
		List<Category> listCategoryParents = new ArrayList<>();
		Category parent = child.getParent();
		while(parent != null){
			
			listCategoryParents.add(0, parent);
			parent = parent.getParent();
		}
		listCategoryParents.add(child);
		return listCategoryParents;
	}

}
