package com.example.royalty.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        int code = (status instanceof Integer) ? (Integer) status : 500;
        model.addAttribute("status", String.valueOf(code));
        if (code == 404) {
            model.addAttribute("message", "Page not found");
        } else if (code == 500) {
            model.addAttribute("message", "Internal server error");
        }
        return "error";
    }
}
