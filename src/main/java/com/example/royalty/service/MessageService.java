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

    public List<Message> getAllFilterByCreateDate(LocalDateTime startDate, LocalDateTime endDate) {
        return messageRepository.findAllByCreateTimeBetween(startDate, endDate);
    }

    public void createBulk(BulkMessageDAO message) {
        for (Long cid : message.getCids()) {
            Message messageSMS = new Message();
            Optional<Customer> optionalCustomer = customerRepository.findById(cid);
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                messageSMS.setMessage(message.getMessage());
                messageSMS.setCid(cid);
                messageSMS.setPhone(customer.getPhone());
                messageSMS.setCreateTime(LocalDateTime.now());
                messageSMS.setSend(false);
                messageRepository.save(messageSMS);
            }
        }
    }
    public void create(Message message) {
        message.setCreateTime(LocalDateTime.now());
        message.setSend(false);
        messageRepository.save(message);
    }

    public List<Message> getUnsentMessages() {
        return messageRepository.findAllBySend(false);
    }

    public void update(Message message) {
        messageRepository.save(message);
    }
}
