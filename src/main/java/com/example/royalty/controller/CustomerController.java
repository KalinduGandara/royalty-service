package com.example.royalty.controller;

import com.example.royalty.modal.Customer;
import com.example.royalty.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    @GetMapping("")
    public String all(Model model) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        return "customer";
    }

    @GetMapping("/create")
    public String createPage(Model model) {

        return "addCustomer";

    }

    @PostMapping("/create")
    public String create(Customer customer) {
        customerService.create(customer);
        return "redirect:/customer";
    }
}
