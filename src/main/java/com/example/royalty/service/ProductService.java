package com.example.royalty.service;

import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import com.example.royalty.repository.CodeRepository;
import com.example.royalty.repository.ProductRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);

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
        product1.setSku(product.getSku());
        product1.setDescription(product.getDescription());
        product1.setPoints(product.getPoints());
        productRepository.save(product1);
        return true;
    }


    public Product getById(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    @Transactional
    public List<Code> generateCode(Product product, int count) {
        List<Code> codes = new ArrayList<>();
        Set<String> uniqueCodes = new HashSet<>();

        while (codes.size() < count) {
            String generatedString = RandomStringUtils.randomAlphanumeric(10).toUpperCase();

            if (uniqueCodes.contains(generatedString) || codeRepository.existsByCode(generatedString)) {
                continue;
            }
            uniqueCodes.add(generatedString);
            Code code = new Code();
            code.setCode(generatedString);
            code.setProduct(product);
            code.setCreatedAt(LocalDateTime.now());
            codeRepository.save(code);
            codes.add(code);
        }
        return codes;
    }

    public List<Code> getCodes(long id) {
        return codeRepository.findAllByProductId(id);
    }

    public List<Code> getCodesByProductIdAndCreatedDate(long product_id, LocalDateTime startDate, LocalDateTime endDate) {
        return codeRepository.findAllByProductIdAndCreatedAtBetween(product_id, startDate, endDate);

    }

    public int createBulk(List<String[]> rows) {
        List<String[]> incompleteRows = new ArrayList<>();
        for (String[] row : rows) {
            if (row[0].isEmpty() || row[3].isEmpty() || row[2].isEmpty()) {
                incompleteRows.add(row);
                continue;
            }
            Product product = productRepository.findByCode(row[1]);
            if (product == null) {
                product = new Product();
                product.setCode(row[0]);
            }
            product.setSku(row[2]);
            product.setDescription(row[1]);
            product.setPoints(Integer.parseInt(row[3]));
            productRepository.save(product);
        }
        for (String[] incompleteRow : incompleteRows) {
            logger.error("Incomplete row: {}", (Object) incompleteRow);
        }
        return incompleteRows.size();
    }

    public Product getByCodes(String codeString) {
        Code code = codeRepository.findByCode(codeString);
        if (code == null) {
            return null;
        }
        return code.getProduct();
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}
