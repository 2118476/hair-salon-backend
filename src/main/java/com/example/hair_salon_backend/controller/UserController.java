package com.example.hair_salon_backend.controller;

import com.example.hair_salon_backend.dto.LoginDTO;
import com.example.hair_salon_backend.dto.ForgotPasswordDTO;
import com.example.hair_salon_backend.dto.ResetPasswordDTO;
import com.example.hair_salon_backend.model.User;
import com.example.hair_salon_backend.service.EmailService;
import com.example.hair_salon_backend.service.UserService;
import com.example.hair_salon_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("Registering user: {}", user.getEmail());
        if (userService.existsByEmail(user.getEmail())) {
            logger.warn("Email is already taken: {}", user.getEmail());
            return ResponseEntity.badRequest().body("Email is already taken");
        }
        userService.saveUser(user);
        logger.info("User registered successfully: {}", user.getEmail());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        Optional<User> userOptional = userService.findByEmail(loginDTO.getEmail());
        if (userOptional.isEmpty() || !userService.checkPassword(loginDTO.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    
        User user = userOptional.get();
        String token = jwtUtil.generateToken(loginDTO.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("role", user.getRole()); // Ensure role is included in the response
        response.put("userId", user.getId()); // Include user ID in the response
        return ResponseEntity.ok(response);
    }
    

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        logger.info("Forgot password for email: {}", forgotPasswordDTO.getEmail());
        boolean emailExists = userService.existsByEmail(forgotPasswordDTO.getEmail());
        if (!emailExists) {
            logger.warn("Email not found: {}", forgotPasswordDTO.getEmail());
            return ResponseEntity.badRequest().body("Email not found");
        }

        // Generate a reset password token (in a real application, this should be more
        // secure)
        String token = jwtUtil.generateToken(forgotPasswordDTO.getEmail());

        // Send email
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendEmail(forgotPasswordDTO.getEmail(), "Reset Password",
                "Click the link to reset your password: " + resetLink);

        return ResponseEntity.ok("Reset password email sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        logger.info("Resetting password for token: {}", resetPasswordDTO.getToken());
        boolean tokenValid = jwtUtil.validateToken(resetPasswordDTO.getToken(), resetPasswordDTO.getEmail());
        if (!tokenValid) {
            logger.warn("Invalid or expired token: {}", resetPasswordDTO.getToken());
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        userService.updatePassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getNewPassword());
        logger.info("Password reset successful for email: {}", resetPasswordDTO.getEmail());
        return ResponseEntity.ok("Password reset successful");
    }

    @GetMapping("/secure-data")
    public ResponseEntity<String> getSecureData(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.extractEmail(token);
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty() || !jwtUtil.validateToken(token, email)) {
            logger.warn("Access forbidden for token: {}", token);
            return ResponseEntity.status(403).body("Forbidden");
        }
        logger.info("Access granted for user: {}", email);
        return ResponseEntity.ok("This is a protected resource");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }
        userService.saveAdmin(user);
        return ResponseEntity.ok("Admin registered successfully");
    }

    ///////////
    @PostMapping("/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable Long userId) {
        try {
            userService.promoteUserToAdmin(userId);
            return ResponseEntity.ok("User promoted to admin successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
/////////
//////
    // UserController.java

@GetMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
}
//////////
//demote admin
@PostMapping("/demote/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> demoteAdminToUser(@PathVariable Long userId) {
    try {
        userService.demoteAdminToUser(userId);
        return ResponseEntity.ok("User demoted to regular user successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

//demote moderator
@PostMapping("/demote-to-user/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> demoteUserToUser(@PathVariable Long userId) {
    try {
        userService.demoteUserToUser(userId);
        return ResponseEntity.ok("User demoted to regular user successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/demote-admin-to-moderator/{userId}")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public ResponseEntity<?> demoteAdminToModerator(@PathVariable Long userId) {
    try {
        userService.demoteAdminToModerator(userId);
        return ResponseEntity.ok().body("Admin has been demoted to moderator successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
@PostMapping("/promote-to-moderator/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> promoteUserToModerator(@PathVariable Long userId) {
    try {
        userService.promoteUserToModerator(userId);
        return ResponseEntity.ok("User promoted to moderator successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@GetMapping("/me")
public ResponseEntity<User> getLoggedInUserProfile(@RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
        token = token.substring(7);
    }

    String email = jwtUtil.extractEmail(token);
    Optional<User> userOptional = userService.findByEmail(email);

    if (userOptional.isPresent() && jwtUtil.validateToken(token, email)) {
        User user = userOptional.get();
        return ResponseEntity.ok(user);
    } else {
        return ResponseEntity.status(403).body(null); // Forbidden if not authenticated or token is invalid
    }
}

}
