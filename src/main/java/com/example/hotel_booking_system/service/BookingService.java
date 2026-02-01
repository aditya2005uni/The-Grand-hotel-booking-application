












package com.example.hotel_booking_system.service;

import com.example.hotel_booking_system.entity.Booking;
import com.example.hotel_booking_system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        if (!booking.getCheckOutDate().isAfter(booking.getCheckInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date.");
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByEmail(email);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    public void deleteBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findByIdAndEmail(bookingId, userEmail)
                .orElseThrow(() -> new RuntimeException("Booking not found or not authorized"));

        bookingRepository.delete(booking);
    }
}