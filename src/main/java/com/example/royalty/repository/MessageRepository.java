package com.example.royalty.repository;

import com.example.royalty.modal.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByCreateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Message> findAllBySend(boolean sent);
}
