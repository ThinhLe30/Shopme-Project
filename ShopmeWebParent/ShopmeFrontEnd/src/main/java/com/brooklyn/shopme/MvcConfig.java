package com.brooklyn.shopme;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		exposeDirectory(registry,"../category-images");
		exposeDirectory(registry,"../brand-logos");
		exposeDirectory(registry,"../product-images");
		exposeDirectory(registry,"../site-logo");
	}

	private void exposeDirectory(ResourceHandlerRegistry registry,String pathPattern) {
		Path path =Paths.get(pathPattern);
		String absolutePath = path.toFile().getAbsolutePath();
		String logicalPath = pathPattern.replace("../", "")+ "/**";
		registry.addResourceHandler(logicalPath)
		.addResourceLocations("file:/" +absolutePath +"/");
	}
	
}
