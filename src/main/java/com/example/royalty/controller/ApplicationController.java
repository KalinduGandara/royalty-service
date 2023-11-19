package com.example.royalty.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class ApplicationController {
    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("message", "Hello World");
        return "welcome";
    }
}

