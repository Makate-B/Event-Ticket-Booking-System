package com.bophelo.event_ticket_booking_system.service;

import com.bophelo.event_ticket_booking_system.dto.request.LoginRequest;
import com.bophelo.event_ticket_booking_system.dto.request.RegisterRequest;
import com.bophelo.event_ticket_booking_system.dto.response.AuthResponse;
import com.bophelo.event_ticket_booking_system.entity.User;
import com.bophelo.event_ticket_booking_system.enums.UserRole;
import com.bophelo.event_ticket_booking_system.exception.DuplicateResourceException;
import com.bophelo.event_ticket_booking_system.mapper.UserMapper;
import com.bophelo.event_ticket_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public String register(RegisterRequest request){

        if (userRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CUSTOMER);
        user.setLocked(false);
        user.setEnabled(true);

        userRepository.save(user);

        return "You registered Successfully";
    }

    public AuthResponse login(LoginRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, "Bearer");

    }
}
