package com.example.royalty.controller;

import com.example.royalty.dao.BulkUploadDAO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String all(Model model, @ModelAttribute("message") String message,@ModelAttribute("error") String error) {
        List<Customer> customers = customerService.getAll();
        model.addAttribute("customers", customers);
        model.addAttribute("upload", new BulkUploadDAO());
        model.addAttribute("message", message);
        model.addAttribute("error", error);
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
    public String uploadFile(@ModelAttribute("upload") BulkUploadDAO upload, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = upload.getFile().getOriginalFilename();
        if (fileName == null || fileName.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Please select a file.");
            return "redirect:/customer";
        }
        Reader reader = new InputStreamReader(upload.getFile().getInputStream());
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> rows = csvReader.readAll();

        int incompleteRows = customerService.createBulk(rows);
        if(incompleteRows>0){
            redirectAttributes.addFlashAttribute("error", "There are "+incompleteRows+" incomplete rows in the file.");
        }
        return "redirect:/customer";
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
