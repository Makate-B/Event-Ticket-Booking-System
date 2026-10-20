
package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.Payment;
import com.bophelo.event_ticket_booking_system.entity.Reservation;
import com.bophelo.event_ticket_booking_system.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByProviderReference(String providerReference);

    boolean existsByReservation(Reservation reservation);

    Optional<Payment> findByReservationAndStatus(Reservation reservation, PaymentStatus status);
}