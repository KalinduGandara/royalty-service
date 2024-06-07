package com.example.royalty.service;

import com.example.royalty.modal.Customer;
import com.example.royalty.modal.Message;
import com.example.royalty.modal.Product;
import com.example.royalty.modal.ReceivedMessage;
import com.example.royalty.repository.ReceivedMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReceivedMessageService {

    private final ReceivedMessageRepository receivedMessageRepository;
    private final ProductService productService;

    private final CustomerService customerService;

    private final MessageService messageService;

    public ReceivedMessageService(ReceivedMessageRepository receivedMessageRepository, ProductService productService, CustomerService customerService, MessageService messageService) {
        this.receivedMessageRepository = receivedMessageRepository;
        this.productService = productService;
        this.customerService = customerService;
        this.messageService = messageService;
    }

    public void create(ReceivedMessage receivedMessage) {
        receivedMessage.setReceivedTime(LocalDateTime.now());
        receivedMessageRepository.save(receivedMessage);
    }

    public void handleReceivedMessages() {
        List<ReceivedMessage> validMessages = receivedMessageRepository.findAllByStatus("valid");
        for (ReceivedMessage receivedMessage : validMessages) {
            receivedMessage.setStatus("processed");

            Product product = productService.findById(receivedMessage.getProductId());
            if (product == null){
                continue;
            }
            Customer customer = customerService.findById(receivedMessage.getCid());
            if (customer == null){
                continue;
            }
            customer.setPoints(customer.getPoints() + product.getPoints());
            customerService.update(customer.getId(), customer);

            Message message = new Message();
            message.setCid(customer.getId());
            message.setPhone(customer.getPhone());
            message.setMessage("You have total " + customer.getPoints() + " points");

            messageService.create(message);
            receivedMessageRepository.save(receivedMessage);
        }
    }
}
