package com.bophelo.event_ticket_booking_system.service;

import com.bophelo.event_ticket_booking_system.dto.response.BookingCancellationResponse;
import com.bophelo.event_ticket_booking_system.dto.response.BookingResponse;
import com.bophelo.event_ticket_booking_system.entity.Booking;
import com.bophelo.event_ticket_booking_system.entity.BookingSeat;
import com.bophelo.event_ticket_booking_system.entity.Reservation;
import com.bophelo.event_ticket_booking_system.entity.ReservationSeat;
import com.bophelo.event_ticket_booking_system.enums.BookingStatus;
import com.bophelo.event_ticket_booking_system.enums.PaymentStatus;
import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import com.bophelo.event_ticket_booking_system.mapper.BookingMapper;
import com.bophelo.event_ticket_booking_system.repository.BookingRepository;
import com.bophelo.event_ticket_booking_system.repository.BookingSeatRepository;
import com.bophelo.event_ticket_booking_system.repository.PaymentRepository;
import com.bophelo.event_ticket_booking_system.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ReservationRepository reservationRepository;
    private final BookingMapper bookingMapper;
    private final PaymentRepository paymentRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Transactional
    public void confirmBooking(String email, Long reservationId) {

        Reservation reservation = reservationRepository.findByIdAndUserEmailWithSeats(reservationId, email)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CONVERTED) {
            throw new IllegalStateException("Reservation has not been successfully paid");
        }

        boolean paymentSuccessful = paymentRepository
                .findByReservationAndStatus(
                        reservation,
                        PaymentStatus.SUCCESS)
                .isPresent();

        if (!paymentSuccessful) {
            throw new IllegalStateException("Successful payment is required to confirm a booking");
        }

        if (bookingRepository.existsByReservation(reservation)) {
            throw new IllegalStateException("A booking already exists for this reservation");
        }

        if (reservation.getReservationSeats().isEmpty()) {
            throw new IllegalStateException("Reservation contains no seats");
        }

        Booking booking = new Booking();
        booking.setReservation(reservation);
        booking.setUser(reservation.getUser());
        booking.setEvent(reservation.getEvent());
        booking.setTotalPrice(reservation.getTotalPrice());
        booking.setBookingNumber(generateAccountNumber());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        for (ReservationSeat reservationSeat
                : reservation.getReservationSeats()) {

            BookingSeat bookingSeat = new BookingSeat();

            bookingSeat.setBooking(savedBooking);
            bookingSeat.setSeat(reservationSeat.getSeat());

            savedBooking.getBookingSeats().add(bookingSeat);

            bookingSeatRepository.save(bookingSeat);
        }

        bookingMapper.toResponse(savedBooking);
    }

    private String generateAccountNumber(){

        String bookingNumber;

        do {
            bookingNumber = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 1_000_000_000_0L));
        }while (
                bookingRepository.existsByBookingNumber(bookingNumber)
        );

        return bookingNumber;
    }

    @Transactional
    public BookingResponse createBookingFromReservation(String email, Long reservationId) {

        Reservation reservation = reservationRepository
                .findByIdAndUserEmailWithSeats(reservationId, email)
                .orElseThrow(() ->
                        new EntityNotFoundException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CONVERTED) {
            throw new IllegalStateException("Reservation has not been successfully paid");
        }

        boolean paymentSuccessful = paymentRepository
                .findByReservationAndStatus(
                        reservation,
                        PaymentStatus.SUCCESS)
                .isPresent();

        if (!paymentSuccessful) {
            throw new IllegalStateException("Successful payment is required to confirm a booking");
        }

        if (bookingRepository.existsByReservation(reservation)) {
            throw new IllegalStateException("A booking already exists for this reservation");
        }

        if (reservation.getReservationSeats().isEmpty()) {
            throw new IllegalStateException("Reservation contains no seats");
        }

        Booking booking = new Booking();
        booking.setUser(reservation.getUser());
        booking.setEvent(reservation.getEvent());
        booking.setTotalPrice(reservation.getTotalPrice());
        booking.setBookingNumber(generateAccountNumber());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        for (var reservationSeat : reservation.getReservationSeats()) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(savedBooking);
            bookingSeat.setSeat(reservationSeat.getSeat());

            bookingSeatRepository.save(bookingSeat);
        }

        return bookingMapper.toResponse(
                bookingRepository.findById(savedBooking.getId())
                        .orElseThrow(() ->
                                new EntityNotFoundException("Booking not found")));

    }

    @Transactional
    public List<BookingResponse> getMyBookings(String email) {

        return bookingRepository
                .findAllByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional
    public BookingCancellationResponse cancelBooking(String email, String bookingNumber) {

        Booking booking = bookingRepository.findByBookingNumberAndUserEmail(bookingNumber, email)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime eventDate = booking.getEvent().getEventDate();

        BigDecimal cancellationFee;

        if (today.isBefore(eventDate)) {
            cancellationFee = booking.getTotalPrice()
                    .multiply(new BigDecimal("0.50"));
        } else {
            cancellationFee = booking.getTotalPrice();
        }

        BigDecimal refundAmount = booking.getTotalPrice()
                .subtract(cancellationFee);

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationFee(cancellationFee);
        booking.setRefundAmount(refundAmount);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toCancellationResponse(savedBooking);

    }

}
