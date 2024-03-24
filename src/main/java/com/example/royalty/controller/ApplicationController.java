package com.example.royalty.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/")
public class ApplicationController {

    @Value("${spring.application.name}")
    String appName;
    @RequestMapping("")
    public String welcome(Map<String, Object> model) {
        model.put("message",appName);
        return "welcomepage";
    }
}

