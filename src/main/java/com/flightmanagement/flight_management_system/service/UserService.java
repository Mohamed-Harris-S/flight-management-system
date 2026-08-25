package com.flightmanagement.flight_management_system.service;

import com.flightmanagement.flight_management_system.dto.RegisterRequest;
import com.flightmanagement.flight_management_system.dto.UserResponse;
import com.flightmanagement.flight_management_system.entity.User;
import com.flightmanagement.flight_management_system.entity.User.Role;
import com.flightmanagement.flight_management_system.exception.DuplicateResourceException;
import com.flightmanagement.flight_management_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException(
                    "User with email '" + request.getEmail() + "' already exists"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);

    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
