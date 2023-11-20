package com.example.royalty.controller;

import com.example.royalty.modal.Product;
import com.example.royalty.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "product";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        return "addProduct";
    }

    @PostMapping("/create")
    public String create(Product product, Model model) {
        productService.create(product);
        return "redirect:/product";
    }




}
