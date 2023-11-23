package com.example.royalty.service;

import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import com.example.royalty.repository.CodeRepository;
import com.example.royalty.repository.ProductRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CodeRepository codeRepository;

    public ProductService(ProductRepository productRepository, CodeRepository codeRepository) {
        this.productRepository = productRepository;
        this.codeRepository = codeRepository;
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

    public boolean generateCode(Product product, int count) {
        for (int i = 0; i < count; i++) {
            String generatedString = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
            Code code = new Code();
            code.setCode(generatedString);
            code.setProduct(product);
            codeRepository.save(code);
        }
        return true;
    }

    public List<Code> getCodes(long id) {
        return codeRepository.findAllByProductId(id);
    }
}
