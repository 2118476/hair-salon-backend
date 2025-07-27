// src/main/java/com/example/hair_salon_backend/service/StylistService.java
package com.example.hair_salon_backend.service;

import com.example.hair_salon_backend.exception.ResourceNotFoundException;
import com.example.hair_salon_backend.model.Stylist;
import com.example.hair_salon_backend.repository.StylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StylistService {

    @Autowired
    private StylistRepository stylistRepository;

    public List<Stylist> getAllStylists() {
        return stylistRepository.findAll();
    }

    public Optional<Stylist> getStylistById(Long id) {
        return stylistRepository.findById(id);
    }

    public Stylist createStylist(Stylist stylist) {
        return stylistRepository.save(stylist);
    }

    public Stylist updateStylist(Long id, Stylist stylistDetails) {
        Stylist stylist = stylistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stylist not found for this id :: " + id));
        stylist.setName(stylistDetails.getName());
        stylist.setSpecialization(stylistDetails.getSpecialization());
        stylist.setAvailability(stylistDetails.getAvailability());
        return stylistRepository.save(stylist);
    }

    public void deleteStylist(Long id) {
        Stylist stylist = stylistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stylist not found for this id :: " + id));
        stylistRepository.delete(stylist);
    }
}
