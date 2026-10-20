package com.bophelo.event_ticket_booking_system.mapper;

import com.bophelo.event_ticket_booking_system.dto.request.RegisterRequest;
import com.bophelo.event_ticket_booking_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUserEntity(RegisterRequest request){

        return new User(
                request.fullName(),
                request.email(),
                request.password());
    }
}
