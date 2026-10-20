package com.bophelo.event_ticket_booking_system.mapper;

import com.bophelo.event_ticket_booking_system.dto.response.PaymentResponse;
import com.bophelo.event_ticket_booking_system.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment){

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getProviderReference(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getReservation().getId());

    }
}
