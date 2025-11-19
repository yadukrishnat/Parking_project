//package com.example.demo.controller;
//
//import com.example.demo.JavaUtils;
//import com.example.demo.dto.LoginRequest;
//import com.example.demo.dto.RegisterRequest;
//import com.example.demo.model.User;
//import com.example.demo.repository.UserRepository;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//public class LoginController {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JavaUtils javaUtils;
//
//    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaUtils javaUtils) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.javaUtils = javaUtils;
//    }
//
//    @PostMapping("/login")
//    public Map<String, Object> login(@RequestBody LoginRequest request) {
//        Map<String, Object> response = new HashMap<>();
//
//        userRepository.findByUsername(request.getUsername()).ifPresentOrElse(user -> {
//            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//                String token = javaUtils.generateToken(user.getUsername());
//                 System.out.println(token);
//                response.put("success", true);
//                response.put("message", "Login successful");
//                response.put("token", token);
//                response.put("name",user.getUsername() ); // Return JWT token
//            } else {
//                response.put("success", false);
//                response.put("message", "Invalid password");
//            }
//        }, () -> {
//            response.put("success", false);
//            response.put("message", "User not found");
//        });
//
//        return response;
//    }
//
//    @PostMapping("/register")
//    public Map<String, Object> register(@RequestBody RegisterRequest request) {
//        Map<String, Object> response = new HashMap<>();
//
//        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
//            response.put("success", false);
//            response.put("message", "Username already exists");
//            return response;
//        }
//
//        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
//        userRepository.save(user);
//
//        response.put("success", true);
//        response.put("message", "User registered successfully");
//        return response;
//    }
//}
//
package com.example.demo.controller;

import com.example.demo.FcmService;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final String FIREBASE_API_KEY = "AIzaSyDtHvmGubOhj3ASbdiov1dAUTiXDpaSCEA";
    private final RestTemplate restTemplate = new RestTemplate();
    private final FcmService fcmService = new FcmService();

    // ---------------------------------------------------------
    // 🔹 LOGIN → call Firebase REST API
    // ---------------------------------------------------------
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

        Map<String, Object> firebaseRequest = new HashMap<>();
        firebaseRequest.put("email", request.getUsername());
        firebaseRequest.put("password", request.getPassword());
        firebaseRequest.put("returnSecureToken", true);

        ResponseEntity<Map> firebaseResponse = restTemplate.postForEntity(url, firebaseRequest, Map.class);

        Map<String, Object> response = new HashMap<>();

        if (firebaseResponse.getStatusCode() == HttpStatus.OK) {
            Map body = firebaseResponse.getBody();

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("idToken", body.get("idToken"));       // JWT Token from Firebase
            response.put("refreshToken", body.get("refreshToken"));
            response.put("localId", body.get("localId"));       // Firebase user ID

        } else {
            response.put("success", false);
            response.put("message", "Firebase login failed");
        }

        return response;
    }


    // ---------------------------------------------------------
    // 🔹 REGISTER → Create Firebase user
    // ---------------------------------------------------------
    @PostMapping("/register")
    public Map<String, Object> register(
            @RequestParam(required = false) String fcmToken,   // 👈 added here
            @RequestBody RegisterRequest request) {

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;

        Map<String, Object> firebaseRequest = new HashMap<>();
        firebaseRequest.put("email", request.getUsername());
        firebaseRequest.put("password", request.getPassword());
        firebaseRequest.put("returnSecureToken", true);

        ResponseEntity<Map> firebaseResponse =
                restTemplate.postForEntity(url, firebaseRequest, Map.class);

        Map<String, Object> response = new HashMap<>();

        if (firebaseResponse.getStatusCode() == HttpStatus.OK) {
            response.put("success", true);
            response.put("message", "User registered successfully");

            // 🔥 Send FCM)
            if (fcmToken != null && !fcmToken.isEmpty()) {
                fcmService.sendNotification(
                        fcmToken,
                        "Welcome " + request.getUsername(),
                        "Your account has been created successfully!"
                );
            }

        } else {
            response.put("success", false);
            response.put("message", "Firebase registration failed");
        }

        return response;
    }


}
