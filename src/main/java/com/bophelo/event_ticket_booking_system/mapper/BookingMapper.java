package com.bophelo.event_ticket_booking_system.mapper;

import com.bophelo.event_ticket_booking_system.dto.response.BookingCancellationResponse;
import com.bophelo.event_ticket_booking_system.dto.response.BookingResponse;
import com.bophelo.event_ticket_booking_system.entity.Booking;
import com.bophelo.event_ticket_booking_system.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking){

        List<String> seatNumbers = booking.getBookingSeats()
                .stream()
                .map(bs -> bs.getSeat().getSeatNumber())
                .toList();

        return new BookingResponse(
                booking.getEvent().getName(),
                booking.getEvent().getVenue().getName(),
                booking.getEvent().getEventDate(),
                seatNumbers,
                booking.getTotalPrice(),
                booking.getBookingNumber(),
                booking.getStatus(),
                booking.getCreatedAt());
    }

    public BookingCancellationResponse toCancellationResponse(Booking booking){

        return new BookingCancellationResponse(
                booking.getBookingNumber(),
                booking.getStatus().name(),
                booking.getTotalPrice(),
                booking.getCancellationFee(),
                booking.getRefundAmount(),
                booking.getCancelledAt()
        );
    }
}
