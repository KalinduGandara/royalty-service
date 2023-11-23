package com.example.royalty.repository;

import com.example.royalty.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNic(String NIC);

    User findByName(String username);
}
