package com.bophelo.event_ticket_booking_system.dto.response;

import com.bophelo.event_ticket_booking_system.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        BigDecimal amount,
        String currency,
        String providerReference,
        PaymentStatus status,
        LocalDateTime createdAt,
        Long reservationId
) {}