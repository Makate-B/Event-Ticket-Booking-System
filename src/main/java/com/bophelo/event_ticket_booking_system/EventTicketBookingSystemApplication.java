package com.bophelo.event_ticket_booking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventTicketBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventTicketBookingSystemApplication.class, args);
	}

}
