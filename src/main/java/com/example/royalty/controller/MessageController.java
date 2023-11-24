package com.example.royalty.controller;

import com.example.royalty.dao.BulkMessageDAO;
import com.example.royalty.modal.Message;
import com.example.royalty.service.CustomerService;
import com.example.royalty.service.MessageService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequestMapping("message")
public class MessageController {
    private final MessageService messageService;
    private final CustomerService customerService;

    public MessageController(MessageService messageService, CustomerService customerService) {
        this.messageService = messageService;
        this.customerService = customerService;
    }
    @GetMapping("")
    public String all(Model model) {
        List<Message> messages = messageService.getAll();
        model.addAttribute("messages", messages);
        return "messages";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("message", new BulkMessageDAO());
        model.addAttribute("customers", customerService.getAll());
        return "addMessage";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("message") BulkMessageDAO message) {
        System.out.println(message);
        messageService.createBulk(message);
        return "redirect:/message";
    }
}
