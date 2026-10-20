package com.bophelo.event_ticket_booking_system.repository;

import com.bophelo.event_ticket_booking_system.entity.Event;
import com.bophelo.event_ticket_booking_system.entity.EventRowPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRowPriceRepository
        extends JpaRepository<EventRowPrice, Long> {

    List<EventRowPrice> findAllByEvent(Event event);
}
