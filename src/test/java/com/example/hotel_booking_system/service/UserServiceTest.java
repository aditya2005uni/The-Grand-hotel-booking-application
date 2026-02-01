package com.example.hotel_booking_system.service;

import com.example.hotel_booking_system.entity.Role;
import com.example.hotel_booking_system.entity.User;
import com.example.hotel_booking_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_validData_shouldSaveUser() {
        String email = "test@example.com";
        String rawPassword = "password123";
        Role role = Role.USER;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User savedUser = userService.registerUser(email, rawPassword, role);

        assertNotNull(savedUser);
        assertEquals(email, savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(role, savedUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_existingEmail_shouldThrowException() {
        String email = "existing@example.com";
        User existingUser = new User(1L, email, "password", Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(email, "password", Role.USER);
        });

        assertEquals("User already exists with email: " + email, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_validUser_shouldReturnUserDetails() {
        String email = "user@example.com";
        User user = new User(1L, email, "encodedPassword", Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_userNotFound_shouldThrowException() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
    }

    @Test
    void findByEmail_existingUser_shouldReturnUser() {
        String email = "test@example.com";
        User user = new User(1L, email, "password", Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

    @Test
    void findByEmail_nonExistingUser_shouldReturnEmpty() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findByEmail(email);

        assertFalse(foundUser.isPresent());
    }
}