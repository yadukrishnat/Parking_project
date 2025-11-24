package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserDto getMyDetails(Authentication authentication) {
        String firebaseUid = authentication.getName();

        // Find existing user or create a new one
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setFirebaseUid(firebaseUid);

                    // Use email if available in principal
                    String email = (authentication.getPrincipal() instanceof com.google.firebase.auth.FirebaseToken token)
                            ? token.getEmail()
                            : firebaseUid; // fallback

                    newUser.setUsername(email);
                    newUser.setUserType("USER");
                    return userRepository.save(newUser);
                });

        // Return a DTO with userId, username, and userType
        return new UserDto(user.getId(), user.getUsername(), user.getUserType(), user.getFirebaseUid());
    }

    // DTO class for API response
    public record UserDto(Long userId, String username, String userType, String firebaseUid) {}
}
