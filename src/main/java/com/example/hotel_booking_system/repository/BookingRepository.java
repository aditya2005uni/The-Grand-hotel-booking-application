// BookingRepository.java
package com.example.hotel_booking_system.repository;

import com.example.hotel_booking_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByEmail(String email);
    Optional<Booking> findByIdAndEmail(Long id, String email);
}
