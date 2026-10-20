package com.bophelo.event_ticket_booking_system.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingCancellationResponse(
        String bookingNumber,
        String status,
        BigDecimal totalPrice,
        BigDecimal cancellationFee,
        BigDecimal refundAmount,
        LocalDateTime cancelledAt
) {}
