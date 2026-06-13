package com.cliniqo.auth.service;

import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.repository.UserRepository;
import com.cliniqo.common.enums.UserStatus;
import com.cliniqo.common.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CredentialService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CredentialService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (user.getStatus() != UserStatus.ACTIVE || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return user;
    }
}
