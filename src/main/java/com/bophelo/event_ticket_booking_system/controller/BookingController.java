package com.bophelo.event_ticket_booking_system.controller;

import com.bophelo.event_ticket_booking_system.dto.response.BookingCancellationResponse;
import com.bophelo.event_ticket_booking_system.dto.response.BookingResponse;
import com.bophelo.event_ticket_booking_system.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        List<BookingResponse> responses = bookingService.getMyBookings(authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/cancel/{bookingNumber}")
    public ResponseEntity<BookingCancellationResponse> cancelBooking(@PathVariable String bookingNumber, Authentication authentication) {
        BookingCancellationResponse bookingCancellation = bookingService.cancelBooking(authentication.getName(), bookingNumber);
        return ResponseEntity.ok(bookingCancellation);
    }
}