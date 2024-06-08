package com.example.royalty.repository;

import com.example.royalty.modal.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByTpNumber(String tpNumber);

    Customer findByPhone(String phoneNumber);
}
