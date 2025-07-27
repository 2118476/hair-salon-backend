package com.example.hair_salon_backend.repository;

import com.example.hair_salon_backend.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<SalonService, Long> {
}
