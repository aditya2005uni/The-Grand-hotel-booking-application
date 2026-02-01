//package com.example.hotel_booking_system.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "users")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//
//    @Column(unique = true, nullable = false)
//    @NotBlank(message = "Email is required")
//    @Pattern(
//            regexp = "^[a-zA-Z0-9._-]+@gmail\\.com$",
//            message = "Email must be a valid Gmail address (e.g., yourname@gmail.com)"
//    )
//    private String email;
//
//
//    @Column(nullable = false)
//    @NotBlank(message = "Password is required")
//    @Size(min = 7, message = "Password must be at least 7 characters long")
//    @Pattern(
//            regexp = ".*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?].*",
//            message = "Password must contain at least one special character (!@#$%^&* etc.)"
//    )
//    private String password;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//}


















package com.example.hotel_booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}






