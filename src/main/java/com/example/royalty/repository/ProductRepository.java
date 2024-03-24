package com.example.royalty.repository;

import com.example.royalty.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCode(String code);

    Product findByCode(String code);
}
