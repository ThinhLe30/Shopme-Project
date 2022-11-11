package com.brooklyn.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.brooklyn.shopme.setting.EmailSettingBag;

public class Utility {
	public static String getSiteURL(HttpServletRequest request ){
		return request.getRequestURL().toString().replace(request.getServletPath(), "");
	}
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(settings.getHost());
		mailSender.setPort(Integer.parseInt(settings.getPort()));
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
		mailSender.setJavaMailProperties(mailProperties);
		return mailSender;
	}
}
