package com.example.royalty.service;

import com.example.royalty.modal.Product;
import com.example.royalty.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public boolean create(Product product) {
        if (validateProduct(product)) {
            productRepository.save(product);
            return true;
        }
        return false;
    }

    private boolean validateProduct(Product product) {
        return product.getName().isEmpty() || product.getCode().isEmpty() || product.getDescription().isEmpty() ;
    }

    public Product getById(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }
}
