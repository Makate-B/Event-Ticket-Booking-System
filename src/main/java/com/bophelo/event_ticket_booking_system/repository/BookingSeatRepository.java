package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
}
