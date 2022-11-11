package com.brooklyn.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.brooklyn.shopme.admin.FileUploadUtil;
import com.brooklyn.shopme.common.entity.Product;
import com.brooklyn.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);
	static void saveUploadedImages(MultipartFile multipartFile, MultipartFile[] extraImageMultiparts,
			Product saveProduct) throws IOException {	
		if(!multipartFile.isEmpty())
		{
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + saveProduct.getId();
			FileUploadUtil.clearDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		if(extraImageMultiparts.length >0)
		{
			String uploadDir = "../product-images/" + saveProduct.getId() +"/extras";
			for(MultipartFile file : extraImageMultiparts)
			{
				if(file.isEmpty()) continue;
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, file);
			}
		}
		
	}
	static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
			
		if(extraImageMultiparts.length >0)
		{
			for(MultipartFile file : extraImageMultiparts)
			{
				if(!file.isEmpty())
				{
					String fileName = StringUtils.cleanPath(file.getOriginalFilename());
					if(!product.containsImageName(fileName)){
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}
	static void setMainImageName(MultipartFile multipartFile, Product product)
	{
		if(!multipartFile.isEmpty())
		{
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
	static void setProductDetails(String[] detailIDs,String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length ==0) return;
		for(int count =0; count < detailNames.length; count ++){
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			if(id != 0){
				product.addDetail(id,name, value);
			}
			else if(!name.isEmpty() && !value.isEmpty()){
				product.addDetail(name,value);
			}
		}
		
	}
	static void deleteExtraImagesWereRemovedOnForm(Product product) {
		String extraImageDir = "../product-images/" + product.getId() + "/extras";
		Path dirPath = Paths.get(extraImageDir);
		
		try {
			Files.list(dirPath).forEach(file -> {
				String fileName = file.toFile().getName();
				if(!product.containsImageName(fileName)){
					try{
						Files.delete(file);
						LOGGER.info("Deleted extra image: "+fileName);
					}
					catch (IOException e1) {
						LOGGER.error("Could not delete extra image "+fileName);
					}
					
				}
			});
		} catch (IOException e) {
			LOGGER.error("Could not list directory "+dirPath);
		}
	}
	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
		if(imageIDs == null || imageIDs.length == 0) return;
		Set<ProductImage> images = new HashSet<>();
		for(int i =0;i < imageIDs.length;i++){
			Integer id = Integer.parseInt(imageIDs[i]);
			String name = imageNames[i];
			images.add(new ProductImage(id,name,product));
		}
		product.setImages(images);
		
	}
}
