// src/main/java/com/example/hair_salon_backend/controller/StylistController.java
package com.example.hair_salon_backend.controller;

import com.example.hair_salon_backend.model.Stylist;
import com.example.hair_salon_backend.service.StylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stylists")
public class StylistController {

    @Autowired
    private StylistService stylistService;

    @GetMapping
    public List<Stylist> getAllStylists() {
        return stylistService.getAllStylists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stylist> getStylistById(@PathVariable Long id) {
        Optional<Stylist> stylist = stylistService.getStylistById(id);
        return stylist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Stylist createStylist(@RequestBody Stylist stylist) {
        return stylistService.createStylist(stylist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stylist> updateStylist(@PathVariable Long id, @RequestBody Stylist stylistDetails) {
        Stylist updatedStylist = stylistService.updateStylist(id, stylistDetails);
        return ResponseEntity.ok(updatedStylist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStylist(@PathVariable Long id) {
        stylistService.deleteStylist(id);
        return ResponseEntity.noContent().build();
    }
}
