package com.example.royalty.repository;

import com.example.royalty.modal.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
