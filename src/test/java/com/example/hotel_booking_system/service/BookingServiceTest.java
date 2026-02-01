package com.example.hotel_booking_system.service;

import com.example.hotel_booking_system.entity.Booking;
import com.example.hotel_booking_system.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_validDates_shouldSaveBooking() {
        Booking booking = new Booking();
        booking.setEmail("user@example.com");
        booking.setCheckInDate(LocalDate.of(2025, 10, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 10, 5));
        booking.setRoomType("Deluxe");

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(1L);
            return savedBooking;
        });

        Booking saved = bookingService.createBooking(booking);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("user@example.com", saved.getEmail());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void createBooking_invalidDates_shouldThrowException() {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2025, 10, 10));
        booking.setCheckOutDate(LocalDate.of(2025, 10, 5));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(booking);
        });

        assertEquals("Check-out date must be after check-in date.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_sameDates_shouldThrowException() {
        Booking booking = new Booking();
        booking.setCheckInDate(LocalDate.of(2025, 10, 5));
        booking.setCheckOutDate(LocalDate.of(2025, 10, 5));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(booking);
        });

        assertEquals("Check-out date must be after check-in date.", exception.getMessage());
    }

    @Test
    void getBookingsByEmail_shouldReturnUserBookings() {
        String email = "user@example.com";
        List<Booking> bookings = Arrays.asList(
                new Booking(1L, email, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 5), "Standard"),
                new Booking(2L, email, LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 3), "Deluxe")
        );

        when(bookingRepository.findByEmail(email)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByEmail(email);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(email, result.get(0).getEmail());
        verify(bookingRepository, times(1)).findByEmail(email);
    }

    @Test
    void getAllBookings_shouldReturnAllBookings() {
        List<Booking> bookings = Arrays.asList(
                new Booking(1L, "user1@example.com", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 5), "Standard"),
                new Booking(2L, "user2@example.com", LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 3), "Suite")
        );

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }
}