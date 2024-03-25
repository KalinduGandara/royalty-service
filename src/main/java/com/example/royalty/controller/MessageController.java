package com.example.royalty.controller;

import com.example.royalty.dao.BulkMessageDAO;
import com.example.royalty.modal.Message;
import com.example.royalty.service.CustomerService;
import com.example.royalty.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public String all(Model model,
                      @RequestParam(required = false, defaultValue = "") String startDate,
                      @RequestParam(required = false, defaultValue = "") String endDate) {
        LocalDateTime startDateTime = parseDate(startDate+" 00:00:00", LocalDateTime.now().minusMonths(1));
        LocalDateTime endDateTime = parseDate(endDate+" 23:59:59", LocalDateTime.now());

        // validate date
        if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime) || startDateTime.isAfter(LocalDateTime.now()) || endDateTime.isAfter(LocalDateTime.now())) {
            startDateTime = LocalDateTime.now().minusMonths(1);
            endDateTime = LocalDateTime.now();
        }
        List<Message> messages = messageService.getAllFilterByCreateDate(startDateTime, endDateTime);
        model.addAttribute("messages", messages);
        model.addAttribute("startDate", startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("endDate", endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "messages";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("message", new BulkMessageDAO());
        model.addAttribute("customers", customerService.getAll());
        return "addMessage";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("message") BulkMessageDAO message, BindingResult result,Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAll());
            model.addAttribute("message", message);
            return "addMessage";
        }
        messageService.createBulk(message);
        return "redirect:/message";
    }

    private LocalDateTime parseDate(String dateString, LocalDateTime defaultValue) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
