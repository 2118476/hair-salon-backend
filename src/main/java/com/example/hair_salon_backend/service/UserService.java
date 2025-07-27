package com.example.hair_salon_backend.service;

import com.example.hair_salon_backend.model.Role;
import com.example.hair_salon_backend.model.User;
import com.example.hair_salon_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

  
    public void saveUser(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.USER); // Set default role if none provided
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updatePassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Additional method for role assignment
    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ADMIN);  // Use enum type here
        userRepository.save(user);
    }
      /////////////
   
    public void promoteUserToAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }
    
    public void demoteAdminToUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (Role.ADMIN.equals(user.getRole())) {
            user.setRole(Role.USER);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User is not an admin");
        }
    }
    
    public void promoteUserToModerator(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.MODERATOR);
        userRepository.save(user);
    }
    public void demoteUserToUser(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        if (!Role.USER.equals(user.getRole())) { // Checks if the user is not already a regular user
            user.setRole(Role.USER);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User is already a regular user");
        }
    }
    ///
    public void demoteAdminToModerator(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new RuntimeException("User not found"));
        if (Role.ADMIN.equals(user.getRole())) { // Ensure the user is currently an admin
            user.setRole(Role.MODERATOR); // Change the role to Moderator
            userRepository.save(user);
        } else {
            throw new RuntimeException("User is not an admin");
        }
    }

    public boolean hasAdmin() {
        return userRepository.findByRole(Role.ADMIN).isPresent();
    }
    
}
