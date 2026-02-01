

package com.example.hotel_booking_system.controller;

import com.example.hotel_booking_system.entity.Role;
import com.example.hotel_booking_system.entity.User;
import com.example.hotel_booking_system.service.UserService;
import com.example.hotel_booking_system.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String roleStr = request.getOrDefault("role", "USER").toUpperCase();

        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role. Allowed values: USER, ADMIN");
        }

        User user = userService.registerUser(email, password, role);
        return ResponseEntity.ok("User registered successfully with email: " + user.getEmail());
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        UserDetails userDetails = userService.loadUserByUsername(email);
        String role = userService.findByEmail(email).get().getRole().name();
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        return ResponseEntity.ok(Map.of("token", token));
    }
}

