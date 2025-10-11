package com.example.demo.repository;


import com.example.demo.model.Land;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandRepository extends JpaRepository<Land, Long> {
    List<Land> findByUserId(Long userId);
}


