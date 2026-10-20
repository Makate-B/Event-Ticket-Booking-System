package com.bophelo.event_ticket_booking_system.dto.response;

public record AuthResponse(
        String token,
        String tokenType
) {
}
