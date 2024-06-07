package com.example.royalty.controller;

import com.example.royalty.dao.SmsRequest;
import com.example.royalty.modal.*;
import com.example.royalty.service.CodeService;
import com.example.royalty.service.CustomerService;
import com.example.royalty.service.MessageService;
import com.example.royalty.service.ReceivedMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SmsController {

    private final CodeService codeService;
    private final CustomerService customerService;

    private final MessageService messageService;

    private final ReceivedMessageService receivedMessageService;
    public SmsController(CustomerService customerService, MessageService messageService, CodeService codeService, ReceivedMessageService receivedMessageService) {
        this.customerService = customerService;
        this.messageService = messageService;
        this.codeService = codeService;
        this.receivedMessageService = receivedMessageService;
    }

    @PostMapping("/sms-mo-callback")
    public ResponseEntity<String> handleSmsMoCallback(@RequestBody SmsRequest smsRequest) {
        // Process the SMS request here
        System.out.println("Received SMS MO callback: " + smsRequest);

        Customer customer = customerService.getByPhone(smsRequest.getPhone_number());
        if (customer == null) {
            ReceivedMessage receivedMessage = new ReceivedMessage();
            receivedMessage.setMessage(smsRequest.getMessage());
            receivedMessage.setPhone(smsRequest.getPhone_number());
            receivedMessage.setStatus("unregistered");
            receivedMessageService.create(receivedMessage);

            Message message = new Message();
            message.setPhone(smsRequest.getPhone_number());
            message.setMessage("Sorry, you are not registered with the Dr.Fixit – Loyalty program.");
            messageService.create(message);
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
        String trimmedMessage = smsRequest.getMessage().replaceAll("\\s", "");
        Code code = codeService.getByCode(trimmedMessage);
        if (code == null) {
            ReceivedMessage receivedMessage = new ReceivedMessage();
            receivedMessage.setMessage(smsRequest.getMessage());
            receivedMessage.setPhone(smsRequest.getPhone_number());
            receivedMessage.setCid(customer.getId());
            receivedMessage.setStatus("invalid");
            receivedMessageService.create(receivedMessage);

            Message message = new Message();
            message.setPhone(smsRequest.getPhone_number());
            message.setCid(customer.getId());
            message.setMessage("This code is not valid. Please check and enter the correct code.");
            messageService.create(message);
            return new ResponseEntity<>("Code is invalid", HttpStatus.NOT_FOUND);
        }
        if(code.isUsed()){

            ReceivedMessage receivedMessage = new ReceivedMessage();
            receivedMessage.setMessage(smsRequest.getMessage());
            receivedMessage.setPhone(smsRequest.getPhone_number());
            receivedMessage.setCid(customer.getId());
            receivedMessage.setStatus("used");
            receivedMessageService.create(receivedMessage);

            Message message = new Message();
            message.setPhone(smsRequest.getPhone_number());
            message.setMessage("This code is already used. Please check and enter the correct code.");
            message.setCid(customer.getId());
            messageService.create(message);
            return new ResponseEntity<>("Code is already used", HttpStatus.NOT_FOUND);
        }
        codeService.markAsUsed(code);
        Product product = code.getProduct();
        ReceivedMessage receivedMessage = new ReceivedMessage();
        receivedMessage.setMessage(smsRequest.getMessage());
        receivedMessage.setPhone(smsRequest.getPhone_number());
        receivedMessage.setCid(customer.getId());
        receivedMessage.setStatus("valid");
        receivedMessage.setProductId(product.getId());
        receivedMessageService.create(receivedMessage);

        // Respond with success status and "Success" text
        String responseText = "Success";
        return new ResponseEntity<>(responseText, HttpStatus.OK);
    }

//    mock api
    @GetMapping("/sms")
    public ResponseEntity<String> handleSmsMtCallback(@RequestParam String q, @RequestParam String destination, @RequestParam String message) {
        // Process the SMS request here
        System.out.println("Received SMS MT callback: " + q + " " + destination + " " + message);

        // Respond with success status and "Success" text
        String responseText = "Success";
        return new ResponseEntity<>(responseText, HttpStatus.OK);
    }
}