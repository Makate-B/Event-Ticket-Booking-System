    package com.bophelo.event_ticket_booking_system.service;

import com.bophelo.event_ticket_booking_system.entity.Payment;
import com.bophelo.event_ticket_booking_system.entity.Reservation;
import com.bophelo.event_ticket_booking_system.enums.PaymentStatus;
import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import com.bophelo.event_ticket_booking_system.repository.PaymentRepository;
import com.bophelo.event_ticket_booking_system.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationExpirationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public ReservationExpirationService(
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void expireReservations() {

        LocalDateTime now = LocalDateTime.now();

        List<Reservation> expiredReservations =
                reservationRepository
                        .findAllByStatusAndExpiresAtLessThanEqual(
                                ReservationStatus.ACTIVE,
                                now);

        for (Reservation reservation : expiredReservations) {

            reservation.setStatus(ReservationStatus.EXPIRED);

            Optional<Payment> pendingPayment =
                    paymentRepository.findByReservationAndStatus(
                            reservation,
                            PaymentStatus.PENDING);

            pendingPayment.ifPresent(payment ->
                    payment.setStatus(PaymentStatus.FAILED));
        }

        reservationRepository.saveAll(expiredReservations);
    }
}