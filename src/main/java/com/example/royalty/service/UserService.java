package com.example.royalty.service;

import com.example.royalty.modal.User;
import com.example.royalty.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public boolean create(User user) {
        if (userRepository.existsByNic(user.getNic())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean update(long id, User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user1 = userOptional.get();
        user1.setName(user.getName());
        user1.setNic(user.getNic());
        user1.setDescription(user.getDescription());
        user1.setRoll(user.getRoll());
        userRepository.save(user1);
        return true;
    }


    public User getById(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }
}
