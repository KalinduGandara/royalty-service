package com.example.royalty.repository;

import com.example.royalty.modal.Code;
import com.example.royalty.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findAllByProduct(Product product);
    List<Code> findAllByProductId(long product_id);
}
