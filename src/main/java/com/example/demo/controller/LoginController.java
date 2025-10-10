package com.example.demo.controller;

import com.example.demo.JavaUtils;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaUtils javaUtils;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaUtils javaUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaUtils = javaUtils;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        userRepository.findByUsername(request.getUsername()).ifPresentOrElse(user -> {
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = javaUtils.generateToken(user.getUsername());
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("name",user.getUsername() ); // Return JWT token
            } else {
                response.put("success", false);
                response.put("message", "Invalid password");
            }
        }, () -> {
            response.put("success", false);
            response.put("message", "User not found");
        });

        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return response;
        }

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "User registered successfully");
        return response;
    }
}

