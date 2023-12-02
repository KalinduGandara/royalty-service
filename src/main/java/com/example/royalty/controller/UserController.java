package com.example.royalty.controller;

import com.example.royalty.modal.User;
import com.example.royalty.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String all(Model model) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("user")User user, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "addUser";
        }
        if (userService.create(user)){
            return "redirect:/user";
        }
        result.rejectValue("nic", "unique", "NIC is already exist.");
        return "addUser";
    }


    @GetMapping("/{id}")
    public String getOne(@PathVariable long id, Model model) {
        User user = userService.getById(id);
        if (user == null) {
            return "redirect:/user";
        }
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable long id, @Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()){
            System.out.println(result.getAllErrors());
            return "user";
        }
        if (userService.update(id,user)) {
            return "redirect:/user";
        }
        return "user";
    }

}
