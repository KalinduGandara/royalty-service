package com.example.royalty.service;

import com.example.royalty.modal.Product;
import com.example.royalty.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public void create(Product product) {
        productRepository.save(product);
    }
}
