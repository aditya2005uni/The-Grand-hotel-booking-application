package com.example.hotel_booking_system.controller;

import com.example.hotel_booking_system.entity.Role;
import com.example.hotel_booking_system.entity.User;
import com.example.hotel_booking_system.service.UserService;
import com.example.hotel_booking_system.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void signup_validRequest_shouldReturnSuccess() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "newuser@example.com");
        request.put("password", "password123");
        request.put("role", "USER");

        User mockUser = new User(1L, "newuser@example.com", "encodedPassword", Role.USER);
        when(userService.registerUser(anyString(), anyString(), any(Role.class))).thenReturn(mockUser);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("User registered successfully")));
    }

    @Test
    void signup_invalidRole_shouldReturnBadRequest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "newuser@example.com");
        request.put("password", "password123");
        request.put("role", "INVALID_ROLE");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid role. Allowed values: USER, ADMIN"));
    }

    @Test
    void login_validCredentials_shouldReturnToken() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "user@example.com");
        request.put("password", "password123");

        User mockUser = new User(1L, "user@example.com", "encodedPassword", Role.USER);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("user@example.com")
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken("user@example.com", "USER")).thenReturn("mock.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"));
    }

    @Test
    void login_invalidCredentials_shouldReturnUnauthorized() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "user@example.com");
        request.put("password", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Bad credentials") {});

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }
}