package com.bophelo.event_ticket_booking_system.service;

import com.bophelo.event_ticket_booking_system.dto.request.ReservationRequest;
import com.bophelo.event_ticket_booking_system.dto.response.ReservationResponse;
import com.bophelo.event_ticket_booking_system.entity.*;
import com.bophelo.event_ticket_booking_system.enums.ReservationStatus;
import com.bophelo.event_ticket_booking_system.exception.DuplicateResourceException;
import com.bophelo.event_ticket_booking_system.exception.ResourceNotFoundException;
import com.bophelo.event_ticket_booking_system.mapper.ReservationMapper;
import com.bophelo.event_ticket_booking_system.repository.EventRepository;
import com.bophelo.event_ticket_booking_system.repository.EventRowPriceRepository;
import com.bophelo.event_ticket_booking_system.repository.ReservationRepository;
import com.bophelo.event_ticket_booking_system.repository.ReservationSeatRepository;
import com.bophelo.event_ticket_booking_system.repository.SeatRepository;
import com.bophelo.event_ticket_booking_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final long RESERVATION_MINUTES = 10;

    private final ReservationRepository reservationRepo;
    private final EventRepository eventRepo;
    private final EventRowPriceRepository eventRowPriceRepo;
    private final SeatRepository seatRepo;
    private final ReservationSeatRepository reservationSeatRepo;
    private final UserRepository userRepo;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationResponse createReservation(String email, ReservationRequest request) {

        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Event event = eventRepo.findById(request.eventId()).orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Venue venue = event.getVenue();

        List<Long> seatIds = request.seatIds();

        Set<Long> uniqueSeatIds = new HashSet<>(seatIds);

        if (uniqueSeatIds.size() != seatIds.size()) {
            throw new DuplicateResourceException("Duplicate seat IDs are not allowed");
        }

        List<Seat> seats = seatRepo.findAllByVenueAndSeatId(venue, seatIds);

        if (seats.size() != seatIds.size()) {
            throw new EntityNotFoundException("One or more seats were not found");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Long> unavailableSeatIds = reservationSeatRepo.findUnavailableSeatIds(
                        seatIds,
                        ReservationStatus.ACTIVE,
                        ReservationStatus.CONVERTED,
                        now);

        if (!unavailableSeatIds.isEmpty()) {
            throw new DuplicateResourceException("One or more selected seats are unavailable");
        }

        List<EventRowPrice> rowPrices = eventRowPriceRepo.findAllByEvent(event);

        Map<String, BigDecimal> pricesByRow = new HashMap<>();

        for (EventRowPrice rowPrice : rowPrices) {
            pricesByRow.put(rowPrice.getSeatRow(), rowPrice.getPrice());
        }

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Seat seat : seats) {

            BigDecimal seatPrice = pricesByRow.get(seat.getSeatRow());

            if (seatPrice == null) {
                throw new EntityNotFoundException("No price configured for row " + seat.getSeatRow() + " for this event");
            }

            totalPrice = totalPrice.add(seatPrice);
        }

        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(now);
        reservation.setExpiresAt(now.plusMinutes(RESERVATION_MINUTES));

        reservation.setTotalPrice(totalPrice);

        Reservation SavedReservation = reservationRepo.save(reservation);

        List<ReservationSeat> reservationSeats = new ArrayList<>();

        for (Seat seat : seats) {

            ReservationSeat reservationSeat = new ReservationSeat();

            reservationSeat.setReservation(reservation);
            reservationSeat.setSeat(seat);

            reservationSeats.add(reservationSeat);
        }

        reservationSeatRepo.saveAll(reservationSeats);

        reservation.setReservationSeats(reservationSeats);

        return reservationMapper.toReservationResponse(reservation);
    }
}