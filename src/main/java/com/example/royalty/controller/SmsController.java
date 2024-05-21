package com.example.royalty.controller;

import com.example.royalty.dao.SmsRequest;
import com.example.royalty.modal.Code;
import com.example.royalty.modal.Customer;
import com.example.royalty.modal.Message;
import com.example.royalty.modal.Product;
import com.example.royalty.service.CodeService;
import com.example.royalty.service.CustomerService;
import com.example.royalty.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SmsController {

    private final CodeService codeService;
    private final CustomerService customerService;

    private final MessageService messageService;

    public SmsController(CustomerService customerService, MessageService messageService, CodeService codeService) {
        this.customerService = customerService;
        this.messageService = messageService;
        this.codeService = codeService;
    }

    @PostMapping("/sms-mo-callback")
    public ResponseEntity<String> handleSmsMoCallback(@RequestBody SmsRequest smsRequest) {
        // Process the SMS request here
        System.out.println("Received SMS MO callback: " + smsRequest);

        Customer customer = customerService.getByPhone(smsRequest.getPhone_number());
        if (customer == null) {
            Message message = new Message();
            message.setPhone(smsRequest.getPhone_number());
            message.setMessage("You are not registered");
            messageService.create(message);
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
        String trimmedMessage = smsRequest.getMessage().replaceAll("\\s", "");
        Code code = codeService.getByCode(trimmedMessage);
        if (code == null) {
            Message message = new Message();
            message.setPhone(smsRequest.getPhone_number());
            message.setMessage("Code is invalid");
            messageService.create(message);
            return new ResponseEntity<>("Code is invalid", HttpStatus.NOT_FOUND);
        }
        if(code.isUsed()){
            Message message = new Message();
            message.setPhone(smsRequest.getPhone_number());
            message.setMessage("Code is already used");
            messageService.create(message);
            return new ResponseEntity<>("Code is already used", HttpStatus.NOT_FOUND);
        }
        codeService.markAsUsed(code);
        Product product = code.getProduct();
        customer.setPoints(customer.getPoints() + product.getPoints());
        customerService.update(customer.getId(), customer);

        Message message = new Message();
        message.setCid(customer.getId());
        message.setPhone(customer.getPhone());
        message.setMessage("You have total " + customer.getPoints() + " points");

        messageService.create(message);

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