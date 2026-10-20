package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.Seat;
import com.bophelo.event_ticket_booking_system.entity.Venue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Seat s
            WHERE s.venue = :venue
              AND s.id IN :seatIds
            ORDER BY s.id
            """)
    List<Seat> findAllByVenueAndSeatId(@Param("venue") Venue venue, @Param("seatIds") Collection<Long> seatIds);

}
