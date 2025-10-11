package com.example.demo.controller;

import com.example.demo.dto.LandRequest;
import com.example.demo.model.Land;
import com.example.demo.model.User;
import com.example.demo.repository.LandRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/land")
public class LandController {

    private final LandRepository landRepository;
    private final UserRepository userRepository;

    public LandController(LandRepository landRepository,UserRepository userRepository) {
        this.landRepository = landRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addLand(
            @RequestParam Long userId,
            @RequestBody LandRequest request) {


        Map<String, Object> response = new HashMap<>();

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create land
        Land land = new Land();
        land.setPlace(request.getPlace());
        land.setLatitude(request.getLatitude());
        land.setLongitude(request.getLongitude());
        land.setAvailableDays(request.getAvailableDays());
        land.setTimeSlots(request.getTimeSlots());
        land.setUnits(request.getUnits());
        land.setUser(user);

        landRepository.save(land);

        response.put("success", true);
        response.put("message", "Land added successfully");
        response.put("data", land);

        return ResponseEntity.ok(response);
    }




    @GetMapping("/all")
    public Map<String, Object> getAllLands() {
        Map<String, Object> response = new HashMap<>();
        response.put("lands", landRepository.findAll());
        return response;
    }
}
