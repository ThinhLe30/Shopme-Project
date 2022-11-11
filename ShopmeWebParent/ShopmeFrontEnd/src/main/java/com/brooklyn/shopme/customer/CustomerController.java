package com.brooklyn.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brooklyn.shopme.Utility;
import com.brooklyn.shopme.common.entity.Country;
import com.brooklyn.shopme.common.entity.Customer;
import com.brooklyn.shopme.setting.EmailSettingBag;
import com.brooklyn.shopme.setting.SettingService;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SettingService settingService;
	@GetMapping("/register")
	public String showRegisterForm(Model theModel, RedirectAttributes redirectAttributes){
		List<Country> listAllCountries = customerService.listAllCountries();
		theModel.addAttribute("listCountries", listAllCountries);
		theModel.addAttribute("pageTitle", "Customer Registration");
		theModel.addAttribute("customer", new Customer());
		return "register/register_form";
	}
	@GetMapping("/verify")
	public String verifyAccount(Model theModel,@Param("code") String code){
		System.out.println(code);
		boolean verified = customerService.verify(code);
		System.out.println(verified);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model theModel,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException{
		customerService.registerCustomer(customer);
		sendVerificationEmail(request, customer);
		
		theModel.addAttribute("pageTitle", "Registration Succeeded!");
		return "register/register_success";
		
	}
	private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(),emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		content = content.replace("[[name]]", customer.getFullName());
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
		content = content.replace("[[URL]]", verifyURL);
		helper.setText(content, true);
		mailSender.send(message);
	}
}
