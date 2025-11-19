package com.example.demo.controller;

import com.example.demo.JavaUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController

    @RequestMapping("/api/user")
    public class UserController {

        private final JavaUtils javaUtils;

        public UserController(JavaUtils javaUtils) {
            this.javaUtils = javaUtils;
        }

        @GetMapping("/me")
        public String getMyEmail(@RequestHeader("Authorization") String authHeader) {
            String token = authHeader.substring(7);
            String email = javaUtils.extractUsername(token);
            return email;
        }
    }





