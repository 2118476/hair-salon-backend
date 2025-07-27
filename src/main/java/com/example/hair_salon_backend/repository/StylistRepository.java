package com.example.hair_salon_backend.repository;

import com.example.hair_salon_backend.model.Stylist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StylistRepository extends JpaRepository<Stylist, Long> {
}
