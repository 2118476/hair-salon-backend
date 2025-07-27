package com.example.hair_salon_backend.service;

import com.example.hair_salon_backend.model.SalonService;
import com.example.hair_salon_backend.repository.ServiceRepository;  // Corrected import statement
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;  // Corrected reference name

    public List<SalonService> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<SalonService> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public SalonService createService(SalonService service) {
        return serviceRepository.save(service);
    }

    public SalonService updateService(Long id, String name, String description, double price, String imageName) {
        Optional<SalonService> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            SalonService service = optionalService.get();
            service.setName(name);
            service.setDescription(description);
            service.setPrice(price);
            return serviceRepository.save(service);
        }
        return null;  // Consider throwing an exception or handling the case where the service isn't found
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    public SalonService updateService(Long id, SalonService service) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateService'");
    }
}
