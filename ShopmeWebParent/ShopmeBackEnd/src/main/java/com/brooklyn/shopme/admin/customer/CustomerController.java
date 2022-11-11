package com.brooklyn.shopme.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brooklyn.shopme.admin.user.UserService;
import com.brooklyn.shopme.common.entity.Country;
import com.brooklyn.shopme.common.entity.Customer;
import com.brooklyn.shopme.common.entity.User;
import com.brooklyn.shopme.common.exception.CustomerNotFoundException;

@Controller
public class CustomerController {
private String defaultRedirectURL = "redirect:/customers/page/1?sortField=firstName&orderBy=asc";
	
	@Autowired private CustomerService service;
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model theModel,
			@Param("sortField") String sortField, 
			@Param("orderBy") String orderBy,
			@Param("keyword") String keyword
			)
	{
		Page<Customer> page = service.listByPage(pageNum,sortField,orderBy,keyword);
		List<Customer> listCustomers = page.getContent();
		String reverseOrderBy = orderBy.equals("asc") ? "desc" : "asc";
		long startCount = (pageNum -1 ) * UserService.USERS_PER_PAGE +1;
		long endCount = startCount + UserService.USERS_PER_PAGE -1;
		if(endCount > page.getTotalElements())
		{
			endCount = page.getTotalElements();
		}
		theModel.addAttribute("totalItems",page.getTotalElements());
		theModel.addAttribute("startCount",startCount);
		theModel.addAttribute("endCount",endCount);
		theModel.addAttribute("currentPage",pageNum);
		theModel.addAttribute("totalPage",page.getTotalPages());
		theModel.addAttribute("sortField",sortField);
		theModel.addAttribute("orderBy",orderBy);
		theModel.addAttribute("keyword",keyword);
		theModel.addAttribute("reverseOrderBy",reverseOrderBy);
		theModel.addAttribute("customers", listCustomers);
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCustomerEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Customer ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return defaultRedirectURL;
	}	
	
	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = service.get(id);
			model.addAttribute("customer", customer);
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;		
		}
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = service.get(id);
			List<Country> countries = service.listAllCountries();
			
			model.addAttribute("listCountries", countries);			
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));
			
			return "customers/customer_form";
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, RedirectAttributes ra) {
		service.save(customer);
		ra.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");
		return defaultRedirectURL;
	}

	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
		try {
			service.delete(id);			
			ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
}
