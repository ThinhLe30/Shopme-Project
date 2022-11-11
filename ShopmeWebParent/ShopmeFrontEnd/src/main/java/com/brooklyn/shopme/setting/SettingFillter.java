package com.brooklyn.shopme.setting;

import java.io.IOException;
import java.util.List;
import java.util.logging.LogRecord;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brooklyn.shopme.common.entity.Setting;
@Component
public class SettingFillter implements Filter, java.util.logging.Filter {

	@Autowired
	private SettingService service;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		if(url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")){
			chain.doFilter(request, response);
			return;
		}
		List<Setting> generalSettings = service.getGeneralSettings();
		generalSettings.forEach(setting -> {
			//System.out.println(setting);
			request.setAttribute(setting.getKey(), setting.getValue());
		});
		chain.doFilter(request, response);

	}

	@Override
	public boolean isLoggable(LogRecord record) {
		// TODO Auto-generated method stub
		return false;
	}

}
