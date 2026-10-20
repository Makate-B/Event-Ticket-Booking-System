package com.bophelo.event_ticket_booking_system.service;

import com.bophelo.event_ticket_booking_system.dto.response.PaymentResponse;
import com.bophelo.event_ticket_booking_system.entity.Payment;
import com.bophelo.event_ticket_booking_system.entity.Reservation;
import com.bophelo.event_ticket_booking_system.enums.PaymentStatus;
import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import com.bophelo.event_ticket_booking_system.mapper.PaymentMapper;
import com.bophelo.event_ticket_booking_system.repository.PaymentRepository;
import com.bophelo.event_ticket_booking_system.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;
    private final BookingService bookingService;

    public PaymentService(
            PaymentRepository paymentRepository,
            ReservationRepository reservationRepository,
            PaymentMapper paymentMapper,
            BookingService bookingService) {

        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentMapper = paymentMapper;
        this.bookingService = bookingService;
    }

    @Transactional
    public PaymentResponse createPendingPayment(String email, Long reservationId) {

        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId).orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You cannot pay for another user's reservation");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }

        if (paymentRepository.existsByReservation(reservation)) {
            throw new IllegalStateException("A payment already exists for this reservation");
        }

        Payment payment = new Payment();
        payment.setAmount(reservation.getTotalPrice());
        payment.setCurrency("ZAR");
        payment.setProviderReference(UUID.randomUUID().toString());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setReservation(reservation);

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse simulatePayment(String email, Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        Reservation reservation = payment.getReservation();

        if (!reservation.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You cannot access another user's payment");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("This payment has already been processed");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }

        LocalDateTime now = LocalDateTime.now();

        if (!reservation.getExpiresAt().isAfter(now)) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            payment.setStatus(PaymentStatus.FAILED);

            reservationRepository.save(reservation);
            paymentRepository.save(payment);

            return paymentMapper.toResponse(payment);
        }

        boolean successful = true;

        if (successful) {

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());

            reservation.setStatus(ReservationStatus.CONVERTED);

            paymentRepository.save(payment);
            reservationRepository.save(reservation);

            bookingService.confirmBooking(email, reservation.getId());

        } else {

            payment.setStatus(PaymentStatus.FAILED);
            reservation.setStatus(ReservationStatus.CANCELLED);

            paymentRepository.save(payment);
            reservationRepository.save(reservation);
        }

        return paymentMapper.toResponse(payment);
    }
}
