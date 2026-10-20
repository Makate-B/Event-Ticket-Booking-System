package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.ReservationSeat;
import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    @Query("""
        SELECT DISTINCT rs.seat.id
        FROM ReservationSeat rs
        WHERE rs.seat.id IN :seatIds
        AND (
            (
                rs.reservation.status = :active
                AND rs.reservation.expiresAt > :now
            )
            OR
            (
                rs.reservation.status = :converted
                AND EXISTS (
                    SELECT b.id
                    FROM Booking b
                    WHERE b.reservation = rs.reservation
                    AND (
                        b.status = com.bophelo.event_ticket_booking_system.enums.BookingStatus.CONFIRMED
                        OR b.status = com.bophelo.event_ticket_booking_system.enums.BookingStatus.COMPLETED
                    )
                )
            )
        )
        """)
    List<Long> findUnavailableSeatIds(
            @Param("seatIds") Collection<Long> seatIds,
            @Param("active") ReservationStatus active,
            @Param("converted") ReservationStatus converted,
            @Param("now") LocalDateTime now
    );
}
