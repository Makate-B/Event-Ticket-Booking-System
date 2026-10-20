package com.bophelo.event_ticket_booking_system.controller;

import com.bophelo.event_ticket_booking_system.dto.response.PaymentResponse;
import com.bophelo.event_ticket_booking_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/reservations/{reservationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponse> createPayment(Authentication authentication, @PathVariable Long reservationId) {
        PaymentResponse result = paymentService.createPendingPayment(authentication.getName(), reservationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/simulate/{paymentId}")
    public ResponseEntity<PaymentResponse> simulatePayment(Authentication authentication, @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.simulatePayment(authentication.getName(), paymentId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
