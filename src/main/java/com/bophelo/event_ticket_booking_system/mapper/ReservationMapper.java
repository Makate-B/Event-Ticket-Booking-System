package com.bophelo.event_ticket_booking_system.mapper;

import com.bophelo.event_ticket_booking_system.dto.response.ReservationResponse;
import com.bophelo.event_ticket_booking_system.entity.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationMapper {

    public ReservationResponse toReservationResponse(
            Reservation reservation) {

        List<String> seatNumbers = reservation.getReservationSeats()
                .stream()
                .map(rs -> rs.getSeat().getSeatNumber())
                .toList();

        return new ReservationResponse(
                reservation.getId(),
                reservation.getEvent().getName(),
                reservation.getEvent().getEventDate(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getExpiresAt(),
                seatNumbers,
                reservation.getTotalPrice()
        );
    }
}
