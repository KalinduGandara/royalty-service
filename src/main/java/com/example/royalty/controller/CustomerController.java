package com.example.royalty.controller;

import com.example.royalty.modal.Customer;
import com.example.royalty.service.CustomerService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
        return "customers";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "addCustomer";

    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("customer")Customer customer, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "addCustomer";
        }
        if (customerService.create(customer)){
            return "redirect:/customer";
        }
        result.rejectValue("nic", "unique", "NIC is already exist.");
        return "addCustomer";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        Reader reader = new InputStreamReader(file.getInputStream());
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> rows = csvReader.readAll();

        customerService.createBulk(rows);
        return "redirect:/";

    }

    @GetMapping("/{id}")
    public String getOne(@PathVariable long id, Model model) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return "redirect:/customer";
        }
        model.addAttribute("customer", customer);
        return "customer";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("customer") Customer customer, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "customer";
        }
        if (customerService.update(id,customer)) {
            return "redirect:/customer";
        }
        return "customer";
    }

}
