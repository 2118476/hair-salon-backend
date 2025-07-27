package com.example.hair_salon_backend.controller;

import com.example.hair_salon_backend.model.SalonService;
import com.example.hair_salon_backend.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    
    @GetMapping
    public ResponseEntity<List<SalonService>> getAllServices() {
        List<SalonService> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalonService> getServiceById(@PathVariable Long id) {
        Optional<SalonService> service = serviceService.getServiceById(id);
        return service.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalonService> createService(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("price") double price,
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            SalonService service = new SalonService();
            service.setName(name);
            service.setDescription(description);
            service.setPrice(price);

         

            SalonService createdService = serviceService.createService(service);
            return ResponseEntity.ok(createdService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalonService> updateService(
        @PathVariable Long id,
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("price") double price
       
    ) {
        try {
            Optional<SalonService> optionalService = serviceService.getServiceById(id);

            if (!optionalService.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            SalonService service = optionalService.get();
            service.setName(name);
            service.setDescription(description);
            service.setPrice(price);

          

            SalonService updatedService = serviceService.updateService(id, service);
            return ResponseEntity.ok(updatedService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }



}
