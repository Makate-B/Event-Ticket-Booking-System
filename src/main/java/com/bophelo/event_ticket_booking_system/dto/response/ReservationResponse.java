package com.bophelo.event_ticket_booking_system.dto.response;

import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long reservationId,
        String eventName,
        LocalDateTime time,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime expiresAt,
        List<String> seatNumbers,
        BigDecimal totalPrice
) {}
