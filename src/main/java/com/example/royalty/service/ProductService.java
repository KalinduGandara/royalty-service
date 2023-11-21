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
        if (productRepository.existsByCode(product.getCode())) {
            return false;
        }
        productRepository.save(product);
        return true;
    }

    public boolean update(long id, Product product) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return false;
        }
        Product product1 = productOptional.get();
        product1.setName(product.getName());
        product1.setCapacity(product.getCapacity());
        product1.setDescription(product.getDescription());
        product1.setPoints(product.getPoints());
        productRepository.save(product1);
        return true;
    }


    public Product getById(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }
}
