package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.LoginRequest;
import com.ecommerce.auth.dto.RegisterRequest;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.repository.UserRepository;
import com.ecommerce.auth.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController                      // Marks this class as REST API controller
@RequestMapping("/auth")              // Base URL → /auth
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Constructor Injection (Spring will auto-inject these)
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    // URL: POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        // Password MUST be encrypted before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER"); // default role
        // Save user in database
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // ================= LOGIN =================
    // URL: POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        // 1️⃣ Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Compare raw password with encrypted password in DB
        boolean passwordMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatch) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        // 3️⃣ Generate JWT token AFTER successful login
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // 4️⃣ Return token to client
        return ResponseEntity.ok(token);
    }
}
