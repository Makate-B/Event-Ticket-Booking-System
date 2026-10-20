package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.Reservation;
import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Query("""
            SELECT DISTINCT r
            FROM Reservation r
            LEFT JOIN FETCH r.reservationSeats rs
            LEFT JOIN FETCH rs.seat
            WHERE r.id = :reservationId
              AND r.user.email = :email
            """)
    Optional<Reservation> findByIdAndUserEmailWithSeats(
            @Param("reservationId") Long reservationId,
            @Param("email") String email
    );

    List<Reservation> findAllByStatusAndExpiresAtLessThanEqual(ReservationStatus reservationStatus, LocalDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);
}
