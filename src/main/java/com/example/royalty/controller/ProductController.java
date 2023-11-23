package com.example.royalty.controller;

import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import com.example.royalty.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public String all(Model model) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("product", new Product());
        return "addProduct";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("product")Product product, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "addProduct";
        }
        if (productService.create(product)){
            return "redirect:/product";
        }
        result.rejectValue("code", "unique", "Code is already exist.");
        return "addProduct";
    }


    @GetMapping("/{id}")
    public String getOne(@PathVariable long id, Model model) {
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/product";
        }
        model.addAttribute("product", product);
        return "product";
    }
    @GetMapping("/{id}/codes")
    public String viewCodes(@PathVariable long id, Model model) {
        List<Code> codes = productService.getCodes(id);
        model.addAttribute("codes", codes);
        return "codes";
    }

    @PostMapping("/{id}/generate")
    public String generateCode(@PathVariable long id, Model model) {
        int count = 1;
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/product";
        }
        model.addAttribute("product", product);
        if(productService.generateCode(product, count)){
            model.addAttribute("message", "Code Generated Successfully.");
        }else {
            model.addAttribute("message", "Code Generation Failed.");
        }
        return "product"+id;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("product") Product product, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "product";
        }
        if (productService.update(id,product)) {
            return "redirect:/product";
        }
        return "product";
    }



}
