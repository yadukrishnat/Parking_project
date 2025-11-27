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
import com.example.demo.dto.RegisterResponseDto;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;



@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

    private static final String FIREBASE_API_KEY = "AIzaSyDtHvmGubOhj3ASbdiov1dAUTiXDpaSCEA";
    private final RestTemplate restTemplate = new RestTemplate();
    private final FcmService fcmService = new FcmService();
    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------
    // 🔹 LOGIN → call Firebase REST API
    // ---------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequest request
    ) {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                + FIREBASE_API_KEY;

        Map<String, Object> firebaseRequest = new HashMap<>();
        firebaseRequest.put("email", request.getUsername());
        firebaseRequest.put("password", request.getPassword());
        firebaseRequest.put("returnSecureToken", true);

        try {
            ResponseEntity<Map> firebaseResponse =
                    restTemplate.postForEntity(url, firebaseRequest, Map.class);

            if (firebaseResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponseDTO(false, "Firebase login failed"));
            }

            Map body = firebaseResponse.getBody();
            String firebaseUid = (String) body.get("localId");

            // Fetch user from DB
            User user = userRepository.findByFirebaseUid(firebaseUid).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new LoginResponseDTO(false, "User not registered"));
            }

            UserController.UserDto userDto = new UserController.UserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getUserType(),
                    user.getFirebaseUid()
            );

            LoginResponseDTO response = new LoginResponseDTO(
                    true,
                    "Login successful",
                    (String) body.get("idToken"),
                    (String) body.get("refreshToken"),
                    firebaseUid,
                    userDto
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO(false, "Invalid username or password"));
        }
    }




    // ---------------------------------------------------------
    // 🔹 REGISTER → Create Firebase user
    // ---------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @RequestBody RegisterRequest request
    ) {

        // ✅ 1. Check if user already exists by username(email)
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponseDto(false, "User already exists"));
        }

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                + FIREBASE_API_KEY;

        Map<String, Object> firebaseRequest = new HashMap<>();
        firebaseRequest.put("email", request.getUsername());
        firebaseRequest.put("password", request.getPassword());
        firebaseRequest.put("returnSecureToken", true);

        try {
            // ✅ 2. Create user in Firebase (token generated here)
            ResponseEntity<Map> firebaseResponse =
                    restTemplate.postForEntity(url, firebaseRequest, Map.class);

            if (firebaseResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponseDto(false, "Firebase registration failed"));
            }

            Map body = firebaseResponse.getBody();
            String firebaseUid = (String) body.get("localId");

            // ✅ 3. Save user in DB
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(null);
            user.setUserType("USER");
            user.setFirebaseUid(firebaseUid);
            userRepository.save(user);

            RegisterResponseDto response = new RegisterResponseDto(
                    true,
                    "User registered successfully",
                    (String) body.get("idToken"),
                    (String) body.get("refreshToken"),
                    firebaseUid
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponseDto(false, "Registration failed"));
        }
    }




}
