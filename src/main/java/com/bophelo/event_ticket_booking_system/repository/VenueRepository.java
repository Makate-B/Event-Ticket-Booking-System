package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    Optional<Venue> findByName(String name);
}
