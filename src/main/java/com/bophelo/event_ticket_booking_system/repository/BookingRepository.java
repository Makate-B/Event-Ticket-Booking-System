package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.Booking;
import com.bophelo.event_ticket_booking_system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    boolean existsByBookingNumber(String bookingNumber);

    boolean existsByReservation(Reservation reservation);

    Optional<Booking> findByReservation(Reservation reservation);

    Optional<Booking> findByBookingNumberAndUserEmail(String bookingNumber, String email);

    List<Booking> findAllByUserEmailOrderByCreatedAtDesc(String email);
}
