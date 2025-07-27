package com.example.hair_salon_backend.controller;

import com.example.hair_salon_backend.model.Role;
import com.example.hair_salon_backend.model.User;
import com.example.hair_salon_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    @Autowired
    private UserService userService;

    @PostMapping("/create-admin")
    public ResponseEntity<?> createInitialAdmin(@RequestBody User adminUser) {
        // Check if an admin already exists
        if (userService.hasAdmin()) {
            return ResponseEntity.badRequest().body("Admin already exists.");
        }

        // Set the role to ADMIN and save the user
        adminUser.setRole(Role.ADMIN);
        userService.saveAdmin(adminUser);

        return ResponseEntity.ok("Admin created successfully.");
    }
}
