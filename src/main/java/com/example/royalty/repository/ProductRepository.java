package com.example.royalty.repository;

import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCode(String code);

    Product findByCode(String code);
}
