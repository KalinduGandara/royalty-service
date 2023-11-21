package com.example.royalty.controller;

import com.example.royalty.modal.Product;
import com.example.royalty.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "addProduct";
    }

    @PostMapping("/create")
    public String create(Product product, Model model) {
        if (productService.create(product)){
            return "redirect:/product";
        }
        return "addProduct";
    }

//    get product by id
    @GetMapping("/{id}")
    public String getOne(@PathVariable long id, Model model) {
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/product";
        }
        model.addAttribute("product", product);
        return "product";
    }



}
