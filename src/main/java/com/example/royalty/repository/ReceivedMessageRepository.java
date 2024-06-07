package com.example.royalty.repository;

import com.example.royalty.modal.ReceivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReceivedMessageRepository extends JpaRepository<ReceivedMessage, Long> {

    List<ReceivedMessage> findAllByStatus(String status);

}
