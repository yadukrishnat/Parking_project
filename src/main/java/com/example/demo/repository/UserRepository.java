package com.example.demo.repository;

import com.example.demo.model.Land;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // OR Option 2: custom JPQL if needed
    @Query("SELECT l FROM Land l WHERE l.user.id = :userId")
    List<Land> getLandsByUserId(Long userId);
}
