package com.example.demo.controller;

import com.example.demo.dto.LandListResponse;
import com.example.demo.dto.LandRequest;
import com.example.demo.model.Land;
import com.example.demo.model.User;
import com.example.demo.repository.LandRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/land")
@CrossOrigin(origins = "*")  // Allow frontend calls
public class LandController {

    private final LandRepository landRepository;
    private final UserRepository userRepository;

    public LandController(LandRepository landRepository, UserRepository userRepository) {
        this.landRepository = landRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addLand(
            @RequestParam Long userId,
            @RequestBody LandRequest request) {

        Map<String, Object> response = new HashMap<>();

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validate request fields
        if (request.getPlace() == null || request.getPlace().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Place is required");
        }
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitude and Longitude are required");
        }
        if (!"LAND_OWNER".equalsIgnoreCase(user.getUserType())) {
            user.setUserType("LAND_OWNER");
            userRepository.save(user);
        }
        // Create land
        Land land = new Land();
        land.setPlace(request.getPlace());
        land.setLatitude(request.getLatitude());
        land.setLongitude(request.getLongitude());
        land.setAvailableDays(String.join(",", request.getAvailableDays()));
        land.setTimeSlots(String.join(",", request.getTimeSlots()));
        land.setUnits(request.getUnits());
        land.setUser(user);

        // Save to database
        Land savedLand = landRepository.save(land);

        response.put("success", true);
        response.put("message", "Land added successfully");
        response.put("data", savedLand);
        response.put("userType",user.getUserType());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/userLands")
    public ResponseEntity<?> getUserLands(@RequestParam Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Get all lands of the user
        List<Land> lands = landRepository.findByUserId(userId);

        // Convert to DTO list
        List<LandListResponse> landResponses = lands.stream()
                .map(land -> new LandListResponse(
                        land.getId(),
                        land.getPlace(),
                        land.getLatitude(),
                        land.getLongitude(),
                        land.getAvailableDays(),
                        land.getTimeSlots(),
                        land.getUnits()
                ))
                .toList();

        return ResponseEntity.ok(landResponses);
    }
    @PutMapping("/activate")
    public ResponseEntity<Map<String, Object>> activateLand(@RequestParam Long landId) {
        Map<String, Object> response = new HashMap<>();

        Land land = landRepository.findById(landId)
                .orElseThrow(() -> new RuntimeException("Land not found"));

        if (Boolean.TRUE.equals(land.isActive())) {  // ✅ check if already active
            response.put("success", false);
            response.put("message", "Land is already active");
            response.put("data", land);
            return ResponseEntity.ok(response);
        }

        // ✅ Activate the land
        land.setActive(true);
        landRepository.save(land);

        response.put("success", true);
        response.put("message", "Land activated successfully");
        response.put("data", land);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/activeland")
    public ResponseEntity<Map<String, Object>> getActiveLands() {
        Map<String, Object> response = new HashMap<>();
        var lands = landRepository.findByActiveTrue();

        response.put("success", true);
        response.put("count", lands.size());
        response.put("data", lands);

        return ResponseEntity.ok(response);
    }

}
