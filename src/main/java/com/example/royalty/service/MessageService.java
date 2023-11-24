package com.example.royalty.service;

import com.example.royalty.dao.BulkMessageDAO;
import com.example.royalty.modal.Customer;
import com.example.royalty.modal.Message;
import com.example.royalty.repository.CustomerRepository;
import com.example.royalty.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    private final CustomerRepository customerRepository;

    public MessageService(MessageRepository messageRepository, CustomerRepository customerRepository) {
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
    }
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    public void createBulk(BulkMessageDAO message) {
        for (Long cid : message.getCids()) {
            Message message1 = new Message();
            Optional<Customer> optionalCustomer = customerRepository.findById(cid);
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                message1.setMessage(message.getMessage());
                message1.setCid(cid);
                message1.setPhone(customer.getPhone());
                message1.setCreateTime(LocalDateTime.now());
                message1.setSend(false);
                messageRepository.save(message1);
            }
        }
    }
}
