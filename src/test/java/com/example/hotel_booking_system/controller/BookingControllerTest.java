package com.example.hotel_booking_system.controller;

import com.example.hotel_booking_system.dto.BookingRequest;
import com.example.hotel_booking_system.entity.Booking;
import com.example.hotel_booking_system.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void bookRoom_validRequest_shouldReturnSuccess() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setCheckInDate(LocalDate.of(2025, 10, 1));
        request.setCheckOutDate(LocalDate.of(2025, 10, 5));
        request.setRoomType("Deluxe");

        Booking mockBooking = new Booking(1L, "user@example.com",
                LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 5), "Deluxe");

        when(bookingService.createBooking(any(Booking.class))).thenReturn(mockBooking);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking confirmed!"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void bookRoom_invalidDates_shouldReturnBadRequest() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setCheckInDate(LocalDate.of(2025, 10, 10));
        request.setCheckOutDate(LocalDate.of(2025, 10, 5));
        request.setRoomType("Standard");

        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new RuntimeException("Check-out date must be after check-in date."));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getMyBookings_shouldReturnUserBookings() throws Exception {
        List<Booking> mockBookings = Arrays.asList(
                new Booking(1L, "user@example.com", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 5), "Standard"),
                new Booking(2L, "user@example.com", LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 3), "Deluxe")
        );

        when(bookingService.getBookingsByEmail("user@example.com")).thenReturn(mockBookings);

        mockMvc.perform(get("/booking/my-bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomType").value("Standard"))
                .andExpect(jsonPath("$[1].roomType").value("Deluxe"));
    }

    @Test
    void bookRoom_unauthenticated_shouldReturnForbidden() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setCheckInDate(LocalDate.of(2025, 10, 1));
        request.setCheckOutDate(LocalDate.of(2025, 10, 5));
        request.setRoomType("Suite");

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
