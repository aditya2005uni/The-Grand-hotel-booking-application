package com.example.hotel_booking_system.controller;

import com.example.hotel_booking_system.entity.Booking;
import com.example.hotel_booking_system.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void allBookings_asAdmin_shouldReturnAllBookings() throws Exception {
        List<Booking> mockBookings = Arrays.asList(
                new Booking(1L, "user1@example.com", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 5), "Standard"),
                new Booking(2L, "user2@example.com", LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 3), "Suite")
        );

        when(bookingService.getAllBookings()).thenReturn(mockBookings);

        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void allBookings_asUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    void allBookings_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isForbidden());
    }
}