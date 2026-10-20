package com.bophelo.event_ticket_booking_system.dto.response;

import com.bophelo.event_ticket_booking_system.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        String eventName,
        String venueName,
        LocalDateTime eventTimeDate,
        List<String> seatNumbers,
        BigDecimal totalPrice,
        String bookingNumber,
        BookingStatus status,
        LocalDate createdAt
) {}
