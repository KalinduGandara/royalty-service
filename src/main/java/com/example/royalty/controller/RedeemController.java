package com.example.royalty.controller;

import com.example.royalty.dao.RedeemPointsDAO;
import com.example.royalty.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/redeem")
public class RedeemController {

    private final CustomerService customerService;

    public RedeemController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("")
    public String all(Model model, @ModelAttribute("message") String message, @ModelAttribute("error") String error) {
        model.addAttribute("customers", customerService.getAll());
        model.addAttribute("points",new RedeemPointsDAO());
        return "redeem";
    }

    @PostMapping("")
    public String redeemCustomers(@Valid @ModelAttribute("points") RedeemPointsDAO dao, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAll());
            model.addAttribute("points", dao);
            return "redeem";
        }
        if (customerService.redeemPoints(dao)) {
            return "redirect:/redeem";
        }
        result.rejectValue("points", "invalid", "Some customers have insufficient points.");
        model.addAttribute("customers", customerService.getAll());
        model.addAttribute("points", dao);
        return "redeem";
    }
}
