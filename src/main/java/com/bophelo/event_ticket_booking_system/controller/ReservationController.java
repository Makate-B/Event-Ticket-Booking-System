package com.bophelo.event_ticket_booking_system.controller;

import com.bophelo.event_ticket_booking_system.dto.request.ReservationRequest;
import com.bophelo.event_ticket_booking_system.dto.response.ReservationResponse;
import com.bophelo.event_ticket_booking_system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<ReservationResponse> createReservation(Authentication authentication, @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }
}