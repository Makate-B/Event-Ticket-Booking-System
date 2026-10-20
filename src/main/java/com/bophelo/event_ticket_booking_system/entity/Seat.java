package com.bophelo.event_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false, length = 1)
    private String seatRow;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @OneToMany(mappedBy = "seat", fetch = FetchType.LAZY)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @OneToMany(mappedBy = "seat", fetch = FetchType.LAZY)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    public Seat(String seatNumber, String seatRow) {
        this.seatNumber = seatNumber;
        this.seatRow = seatRow;
    }
}